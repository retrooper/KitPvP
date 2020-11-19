package net.foulest.kitpvp.listeners;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.Spawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnTimer implements Runnable {

    private final Spawn spawn = Spawn.getInstance();
    private final KitPvP kitPvP = KitPvP.getInstance();
    private final CombatLog combatLog = CombatLog.getInstance();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PendingSpawn ps = kitPvP.spawnTimers.get(player.getUniqueId());

            // Ignores players that aren't teleporting to spawn.
            if (ps == null) {
                continue;
            }

            // Sends the player an action bar.
            if (Math.round((double) (ps.getTeleportTime() - System.currentTimeMillis()) / 1000) > 0) {
                MiscUtils.sendActionbar(player, MiscUtils.colorize("&aTeleporting to spawn in "
                        + Math.round((double) (ps.getTeleportTime() - System.currentTimeMillis()) / 1000) + " seconds..."));
            }

            // Cancels teleports if the player enters combat.
            if (combatLog.isInCombat(player)) {
                kitPvP.spawnTimers.remove(player.getUniqueId());
                MiscUtils.sendActionbar(player, MiscUtils.colorize("&cTeleportation cancelled, you entered combat."));
                continue;
            }

            // Cancels teleports if the player starts moving.
            if (player.getLocation().getBlockX() != ps.getTeleportLocation().getBlockX()
                    || player.getLocation().getBlockY() != ps.getTeleportLocation().getBlockY()
                    || player.getLocation().getBlockZ() != ps.getTeleportLocation().getBlockZ()) {
                kitPvP.spawnTimers.remove(player.getUniqueId());
                MiscUtils.sendActionbar(player, MiscUtils.colorize("&cTeleportation cancelled, you moved."));
                continue;
            }

            // Teleports the player to spawn.
            if (ps.getTeleportTime() < System.currentTimeMillis()) {
                spawn.teleport(player);
                kitPvP.spawnTimers.remove(player.getUniqueId());
                MiscUtils.sendActionbar(player, MiscUtils.colorize("&aTeleported to spawn."));
            }
        }
    }

    @AllArgsConstructor
    @Getter
    public static class PendingSpawn {

        private final long teleportTime;
        private final Location teleportLocation;
    }
}
