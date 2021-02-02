package net.foulest.kitpvp;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import net.foulest.kitpvp.cmds.*;
import net.foulest.kitpvp.kits.*;
import net.foulest.kitpvp.listeners.*;
import net.foulest.kitpvp.utils.*;
import net.foulest.kitpvp.utils.command.CommandFramework;
import net.foulest.kitpvp.utils.kits.Kit;
import net.foulest.kitpvp.utils.kits.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class KitPvP extends JavaPlugin {

    private static KitPvP instance;
    private CommandFramework framework;
    private HikariDataSource hikari;

    public static KitPvP getInstance() {
        return instance;
    }

    @Override
    @SneakyThrows
    public void onEnable() {
        instance = this;
        framework = new CommandFramework(this);

        // Registers placeholders with PlaceholderAPI.
        new Placeholders().register();

        // Creates the default config.
        ConfigManager.setup();
        ConfigManager.get().addDefault("spawn.world", "world");
        ConfigManager.get().addDefault("spawn.x", 0.5);
        ConfigManager.get().addDefault("spawn.y", 64.0);
        ConfigManager.get().addDefault("spawn.z", 0.5);
        ConfigManager.get().addDefault("spawn.yaw", 90.0);
        ConfigManager.get().addDefault("spawn.pitch", 0.0);
        ConfigManager.get().addDefault("coins.kill-bonus", 10);
        ConfigManager.get().addDefault("coins.killstreak-bonus", 5);
        ConfigManager.get().addDefault("mysql.host", "host");
        ConfigManager.get().addDefault("mysql.user", "user");
        ConfigManager.get().addDefault("mysql.password", "password");
        ConfigManager.get().addDefault("mysql.database", "database");
        ConfigManager.get().addDefault("mysql.port", "port");
        ConfigManager.get().options().copyDefaults(true);
        ConfigManager.save();

        // Sets up the MySQL database.
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", ConfigManager.get().getString("mysql.host"));
        hikari.addDataSourceProperty("port", ConfigManager.get().getString("mysql.port"));
        hikari.addDataSourceProperty("databaseName", ConfigManager.get().getString("mysql.database"));
        hikari.addDataSourceProperty("user", ConfigManager.get().getString("mysql.user"));
        hikari.addDataSourceProperty("password", ConfigManager.get().getString("mysql.password"));
        hikari.addDataSourceProperty("characterEncoding", "utf8");
        hikari.addDataSourceProperty("useUnicode", "true");

        // Creates missing tables in the MySQL database.
        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS PlayerStats (uuid VARCHAR(36), coins int, " +
                    "experience int, kills int, deaths int, killstreak int, topKillstreak int)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS PlayerKits (uuid VARCHAR(36), kitId int)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Registers a plugin messaging brand listener.
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "MC|Brand", new BrandListener());

        // Loads the plugin's listeners.
        loadListeners(new DeathListener(), new EventListener(), new KitListener());

        // Loads the plugin's commands.
        loadCommands(new BalanceCmd(), new ClearKitCmd(), new CombatLogCmd(), new EcoGiveCmd(), new EcoSetCmd(),
                new KitsCmd(), new PayCmd(), new SetSpawnCmd(), new SpawnCmd(), new StatsCmd(), new KitShopCmd(),
                new StaffCmd(), new EcoTakeCmd(), new ArmorColorCmd());

        // Loads the plugin's kits.
        loadKits(new Archer(), new Burrower(), new Cactus(), new Dragon(), new Fisherman(), new Ghost(), new Tamer(),
                new Hulk(), new Imprisoner(), new Kangaroo(), new Knight(), new Mage(), new Monk(), new Ninja(), new Pyro(),
                new Spiderman(), new Summoner(), new Tank(), new Thor(), new Timelord(), new Vampire(), new Zen());

        // Loads the spawn.
        Spawn.getInstance().load();

        // Loads online players' user data.
        for (Player player : Bukkit.getOnlinePlayers()) {
            KitUser.getInstance(player).load();
            Spawn.getInstance().teleport(player);
            player.getInventory().setHeldItemSlot(0);
        }
    }

    @Override
    public void onDisable() {
        // Unloads the kits saved in the Kit Manager.
        KitManager.getInstance().unloadKits();

        // Saves the spawn.
        Spawn.getInstance().save();

        // Saves online players' user data.
        for (Player player : Bukkit.getOnlinePlayers()) {
            KitUser.getInstance(player).saveAll();

            if (CombatLog.getInstance().isInCombat(player)) {
                CombatLog.getInstance().remove(player);
            }
        }

        // Closes the MySQL connection.
        if (hikari != null) {
            hikari.close();
        }
    }

    public HikariDataSource getHikari() {
        return hikari;
    }

    public void giveDefaultItems(Player player) {
        KitUser user = KitUser.getInstance(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), () -> {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            ItemStack kitSelector = new ItemBuilder(Material.NETHER_STAR).name("&aKit Selector &7(Right Click)").build();
            player.getInventory().setItem(0, kitSelector);

            ItemStack shopSelector = new ItemBuilder(Material.ENDER_CHEST).name("&aKit Shop &7(Right Click)").build();
            player.getInventory().setItem(1, shopSelector);

            if (user.hasPreviousKit()) {
                ItemStack previousKit = new ItemBuilder(Material.WATCH).name("&aPrevious Kit &7(Right Click)").build();
                player.getInventory().setItem(2, previousKit);
            }

            ItemStack yourStats = new ItemBuilder(SkullCreator.itemFromUuid(player.getUniqueId())).name("&aYour Stats &7(Right Click)").build();
            player.getInventory().setItem(4, yourStats);

            if (player.hasPermission("kitpvp.staff")) {
                ItemStack staffMode = new ItemBuilder(Material.EYE_OF_ENDER).name("&aStaff Mode &7(Right Click)").build();
                player.getInventory().setItem(8, staffMode);
            }

            player.updateInventory();
        }, 1L);
    }

    /**
     * Loads the plugin's listeners.
     *
     * @param listeners Listener to load.
     */
    private void loadListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * Loads the plugin's commands.
     *
     * @param commands Command to load.
     */
    private void loadCommands(Object... commands) {
        for (Object command : commands) {
            framework.registerCommands(command);
        }
    }

    /**
     * Loads the plugin's kits.
     *
     * @param kits Kit to load.
     */
    private void loadKits(Kit... kits) {
        for (Kit kit : kits) {
            KitManager.getInstance().registerKit(kit);
        }
    }
}
