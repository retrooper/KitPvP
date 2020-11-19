package net.foulest.kitpvp;

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitPvP extends JavaPlugin {

    private static KitPvP instance;
    public Map<UUID, SpawnTimer.PendingSpawn> spawnTimers;
    private CommandFramework framework;
    private ConfigManager config;
    private ConfigManager defaultPlayer;

    public static KitPvP getInstance() {
        return instance;
    }

    @SneakyThrows
    public void onEnable() {
        instance = this;
        spawnTimers = new HashMap<>();
        framework = new CommandFramework(this);
        config = new ConfigManager("config", null);
        defaultPlayer = new ConfigManager("default-player", null);

        loadListeners(new EventListener(), new KitListener(), new DeathListener(), new RegionUtil());

        loadCommands(new BalanceCmd(), new ClearKitCmd(), new CombatLogCmd(), new EcoGiveCmd(), new EcoSetCmd(), new KitsCmd(),
                new PayCmd(), new SetSpawnCmd(), new SetEndSpawnCmd(), new SpawnCmd(), new StatsCmd(), new KitShopCmd());

        loadKits(new Archer(), new Burrower(), new Cactus(), new Dragon(), new Fisherman(), new Ghost(), new Tamer(),
                new Hulk(), new Imprisoner(), new Kangaroo(), new Knight(), new Mage(), new Monk(), new Ninja(), new Pyro(),
                new Spiderman(), new Summoner(), new Tank(), new Thor(), new Timelord(), new Vampire(), new Zen());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new Placeholders().register();

        Spawn.getInstance().load();
        EndSpawn.getInstance().load();

        getServer().getScheduler().runTaskTimer(this, new SpawnTimer(), 0L, 5L);

        for (Player player : Bukkit.getOnlinePlayers()) {
            KitUser.getInstance(player).load();
            Spawn.getInstance().teleport(player);
        }
    }

    @Override
    public void onDisable() {
        KitManager.getInstance().unloadKits();
        Spawn.getInstance().save();
        EndSpawn.getInstance().load();

        for (Player player : Bukkit.getOnlinePlayers()) {
            KitUser.getInstance(player).save();
            if (CombatLog.getInstance().isInCombat(player)) {
                CombatLog.getInstance().remove(player);
            }
        }
    }

    public ConfigManager getConfigFile() {
        return config;
    }

    public ConfigManager getDefaultPlayerFile() {
        return defaultPlayer;
    }

    public void giveDefaultItems(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), () -> {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            KitUser user = KitUser.getInstance(player);

            ItemStack kitSelector = new ItemBuilder(Material.NETHER_STAR).name("&aKit Selector &7(Right Click)").build();
            player.getInventory().setItem(0, kitSelector);

            ItemStack shopSelector = new ItemBuilder(Material.ENDER_CHEST).name("&aKit Shop &7(Right Click)").build();
            player.getInventory().setItem(2, shopSelector);

            ItemStack backToHub = new ItemBuilder(Material.BED).name("&cReturn to Hub &7(Right Click)").build();
            player.getInventory().setItem(8, backToHub);

            if (user.hasPreviousKit()) {
                ItemStack previousKit = new ItemBuilder(Material.WATCH).name("&aPrevious Kit: &e" + user.getPreviousKit().getName()).build();
                player.getInventory().setItem(4, previousKit);
            }
        }, 1L);
    }

    private void loadListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    private void loadCommands(Object... objects) {
        for (Object object : objects) {
            framework.registerCommands(object);
        }
    }

    private void loadKits(Kit... kits) {
        for (Kit kit : kits) {
            KitManager.getInstance().registerKit(kit);
        }
    }
}
