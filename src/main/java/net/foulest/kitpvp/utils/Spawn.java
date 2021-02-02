package net.foulest.kitpvp.utils;

import net.foulest.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.logging.Logger;

public class Spawn {

    private static final Spawn instance = new Spawn();
    private final Logger log = Logger.getLogger("Minecraft");
    private final KitPvP kitPvP = KitPvP.getInstance();
    private Location location;

    public static Spawn getInstance() {
        return instance;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location loc) {
        location = loc;
        loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Teleports a player to spawn.
     *
     * @param player The player to teleport.
     */
    public void teleport(Player player) {
        KitUser kitUser = KitUser.getInstance(player);

        if (location == null) {
            MiscUtils.messagePlayer(player, "&cThe spawn point is not set. Please contact an administrator.");
            return;
        }

        kitUser.clearCooldowns();
        kitUser.setKit(null);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.setHealth(20);
        kitPvP.giveDefaultItems(player);

        player.teleport(location);
        player.teleport(new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                player.getLocation().getZ(), location.getYaw(), location.getPitch())); // Might fix the yaw bug.
    }

    /**
     * Saves the spawn point data into the config files.
     */
    public void save() {
        if (location == null) {
            return;
        }

        ConfigManager.get().set("spawn.world", location.getWorld().getName());
        ConfigManager.get().set("spawn.x", location.getX());
        ConfigManager.get().set("spawn.y", location.getY());
        ConfigManager.get().set("spawn.z", location.getZ());
        ConfigManager.get().set("spawn.yaw", location.getYaw());
        ConfigManager.get().set("spawn.pitch", location.getPitch());
        ConfigManager.save();

        log.info("[KitPvP] Spawn saved successfully.");
    }

    /**
     * Loads the spawn point data from config files.
     */
    public void load() {
        if (ConfigManager.get().get("spawn") == null) {
            log.severe("[KitPvP] Spawn is not defined. Define it using /setspawn.");
            return;
        }

        World world = Bukkit.getWorld(ConfigManager.get().getString("spawn.world"));
        double x = ConfigManager.get().getDouble("spawn.x");
        double y = ConfigManager.get().getDouble("spawn.y");
        double z = ConfigManager.get().getDouble("spawn.z");
        float yaw = ConfigManager.get().getInt("spawn.yaw");
        float pitch = ConfigManager.get().getInt("spawn.pitch");
        location = new Location(world, x, y, z, yaw, pitch);
    }
}
