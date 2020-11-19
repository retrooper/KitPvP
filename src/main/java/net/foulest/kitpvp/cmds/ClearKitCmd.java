package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.listeners.CombatLog;
import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.RegionUtil;
import net.foulest.kitpvp.utils.Spawn;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearKitCmd {

	private final Spawn spawn = Spawn.getInstance();
	private final CombatLog combatLog = CombatLog.getInstance();

	@Command(name = "clearkit", description = "Clears your kit.", usage = "/clearkit", permission = "kitpvp.clearkit.self", inGameOnly = true)
	public void onCommand(CommandArgs args) {
		if (!(args.getSender() instanceof Player)) {
			MiscUtils.messagePlayer(args.getSender(), "Only players can execute this command.");
			return;
		}

		Player sender = args.getPlayer();
		KitUser senderData = KitUser.getInstance(sender);

		// Clearing your own kit.
		if (args.length() == 0) {
			if (combatLog.isInCombat(args.getPlayer())) {
				MiscUtils.messagePlayer(args.getPlayer(), "&cYou may not use this command while in combat.");
				return;
			}

			if (RegionUtil.isInSafezone(sender, sender.getLocation())) {
				if (!senderData.hasKit()) {
					MiscUtils.messagePlayer(sender, "&cYou do not have a kit selected.");
					return;
				}

				spawn.teleport(sender);
				MiscUtils.messagePlayer(sender, "&aYour kit has been cleared.");
				return;
			}

			MiscUtils.messagePlayer(sender, "&cYou need to be in spawn to clear your kit.");
			return;
		}

		// Clearing kits from other players.
		if (args.getPlayer().hasPermission("kitpvp.clearkit.others")) {
			Player target = Bukkit.getPlayer(args.getArgs(1));

			if (target == null) {
				MiscUtils.messagePlayer(sender, "&cThat player is not online.");
				return;
			}

			KitUser targetData = KitUser.getInstance(target);
			if (!targetData.hasKit()) {
				MiscUtils.messagePlayer(target, "&cYou do not have a kit selected.");
				return;
			}

			spawn.teleport(target);
			MiscUtils.messagePlayer(target, "&aYour kit has been cleared by a staff member.");
			MiscUtils.messagePlayer(sender, "&aYou cleared " + target.getName() + "'s kit.");
		}
	}
}
