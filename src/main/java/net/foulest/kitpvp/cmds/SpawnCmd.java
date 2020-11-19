package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.listeners.CombatLog;
import net.foulest.kitpvp.listeners.SpawnTimer;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.RegionUtil;
import net.foulest.kitpvp.utils.Spawn;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class SpawnCmd {

	private final Spawn spawn = Spawn.getInstance();
	private final KitPvP kitPvP = KitPvP.getInstance();
	private final CombatLog combatLog = CombatLog.getInstance();

	@Command(name = "spawn", description = "Teleports you to spawn.", usage = "/spawn", inGameOnly = true)
	public void onCommand(CommandArgs args) {
		Player player = args.getPlayer();

		if (combatLog.isInCombat(player)) {
			MiscUtils.messagePlayer(args.getPlayer(), "&cYou may not use this command while in combat.");
			return;
		}

		if (RegionUtil.isInSafezone(player, player.getLocation())) {
			spawn.teleport(player);
			MiscUtils.sendActionbar(player, MiscUtils.colorize("&aTeleported to spawn."));
			return;
		}

		if (kitPvP.spawnTimers.containsKey(player.getUniqueId())) {
			MiscUtils.messagePlayer(player, "&cYou are already teleporting to spawn.");
		} else {
			kitPvP.spawnTimers.put(player.getUniqueId(), new SpawnTimer.PendingSpawn(System.currentTimeMillis() + 5000L, player.getLocation()));
		}
	}
}
