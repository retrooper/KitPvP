package net.foulest.kitpvp.utils;

import net.foulest.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class EndSpawn {

	private static final EndSpawn instance = new EndSpawn();
	private final Logger log = Logger.getLogger("Minecraft");
	private final KitPvP kitPvP = KitPvP.getInstance();
	private final ConfigManager config = kitPvP.getConfigFile();
	private Location location;

	public static EndSpawn getInstance() {
		return instance;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location loc) {
		location = loc;
		loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public void teleport(Player player) {
		if (location == null) {
			MiscUtils.messagePlayer(player, "&cThe end spawn point is not set. Please contact an administrator.");
			return;
		}

		player.teleport(location);
	}

	public void save() {
		if (location == null) {
			return;
		}

		config.set("end-spawn.world", location.getWorld().getName());
		config.set("end-spawn.x", location.getX());
		config.set("end-spawn.y", location.getY());
		config.set("end-spawn.z", location.getZ());
		config.set("end-spawn.yaw", location.getYaw());
		config.set("end-spawn.pitch", location.getPitch());
		config.save();

		log.info(String.format("[%s] End spawn saved successfully.", kitPvP.getDescription().getName()));
	}

	/**
	 * Loads the spawn point data from config files.
	 */
	public void load() {
		if (config.get("end-spawn") == null) {
			log.severe(String.format("[%s] End spawn is not defined. Define it using /setspawn.", kitPvP.getDescription().getName()));
			return;
		}

		World world = Bukkit.getWorld(config.getString("end-spawn.world"));
		double x = config.getDouble("end-spawn.x");
		double y = config.getDouble("end-spawn.y");
		double z = config.getDouble("end-spawn.z");
		float yaw = config.getInt("end-spawn.yaw");
		float pitch = config.getInt("end-spawn.pitch");
		location = new Location(world, x, y, z, yaw, pitch);
	}
}
