package net.foulest.kitpvp.utils;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RegionUtil implements Listener {

	public static final Set<UUID> PLAYERS_IN_REGIONS = new HashSet<>();

	public static boolean isInRegion(Player player, Location loc) {
		WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
		RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(loc);

		for (ProtectedRegion region : set) {
			return region != null;
		}

		return false;
	}

	public static boolean isInSafezone(Player player, Location loc) {
		WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
		RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(loc);

		if (isInRegion(player, loc)) {
			for (ProtectedRegion region : set) {
				return region.getFlag(DefaultFlag.PVP) == StateFlag.State.DENY;
			}
		}

		return false;
	}

	public void handleMove(Player player, Location loc) {
		if (!player.isDead()) {
			if (RegionUtil.isInRegion(player, loc) && !PLAYERS_IN_REGIONS.contains(player.getUniqueId())) {
				PLAYERS_IN_REGIONS.add(player.getUniqueId());
			} else if (!RegionUtil.isInRegion(player, loc)) {
				PLAYERS_IN_REGIONS.remove(player.getUniqueId());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent event) {
		handleMove(event.getPlayer(), event.getTo());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		handleMove(event.getPlayer(), event.getTo());
	}
}
