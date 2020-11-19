package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.listeners.CombatLog;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;

public class CombatLogCmd {

	private final CombatLog combatLog = CombatLog.getInstance();

	@Command(name = "combatlog", aliases = {"combattag", "ct", "combat", "combattime"}, description = "Displays your current combat tag timer.", usage = "/combatlog", permission = "kitpvp.combatlog", inGameOnly = true)
	public void onCommand(CommandArgs args) {
		if (combatLog.isInCombat(args.getPlayer())) {
			int timeLeft = combatLog.getRemainingTime(args.getPlayer());

			MiscUtils.messagePlayer(args.getPlayer(), "&cYou are in combat for " + timeLeft + " more " + (timeLeft == 1 ? "second" : "seconds") + ".");
		} else {
			MiscUtils.messagePlayer(args.getPlayer(), "&aYou are not in combat.");
		}
	}
}
