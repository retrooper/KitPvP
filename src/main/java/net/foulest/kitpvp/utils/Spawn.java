package net.foulest.kitpvp.utils;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.listeners.KitListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.logging.Logger;

public class Spawn {

	private static final Spawn instance = new Spawn();
	private final Logger log = Logger.getLogger("Minecraft");
	private final KitPvP kitPvP = KitPvP.getInstance();
	private final ConfigManager config = kitPvP.getConfigFile();
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

	public void teleport(Player player) {
		if (location == null) {
			MiscUtils.messagePlayer(player, "&cThe spawn point is not set. Please contact an administrator.");
			return;
		}

		KitUser user = KitUser.getInstance(player);

		user.setKit(null);
		user.clearCooldowns();

		player.setHealth(20);
		player.getInventory().setHeldItemSlot(0);
		player.setMetadata("noFall", new FixedMetadataValue(kitPvP, true));

		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		KitListener.DRAINED_EFFECTS.remove(player);

		kitPvP.giveDefaultItems(player);

		player.teleport(location);
	}

	public void save() {
		if (location == null) {
			return;
		}

		config.set("spawn.world", location.getWorld().getName());
		config.set("spawn.x", location.getX());
		config.set("spawn.y", location.getY());
		config.set("spawn.z", location.getZ());
		config.set("spawn.yaw", location.getYaw());
		config.set("spawn.pitch", location.getPitch());
		config.save();

		log.info("[KitPvP] Spawn saved successfully.");
	}

	/**
	 * Loads the spawn point data from config files.
	 */
	public void load() {
		if (config.get("spawn") == null) {
			log.severe("[KitPvP] Spawn is not defined. Define it using /setspawn.");
			return;
		}

		World world = Bukkit.getWorld(config.getString("spawn.world"));
		double x = config.getDouble("spawn.x");
		double y = config.getDouble("spawn.y");
		double z = config.getDouble("spawn.z");
		float yaw = config.getInt("spawn.yaw");
		float pitch = config.getInt("spawn.pitch");
		location = new Location(world, x, y, z, yaw, pitch);
	}
}
