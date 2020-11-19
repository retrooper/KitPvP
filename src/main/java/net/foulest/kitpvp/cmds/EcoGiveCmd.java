package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EcoGiveCmd {

	@Command(name = "ecogive", description = "Adds to the balance of a player.", usage = "/ecogive <player> <amount>", permission = "kitpvp.ecogive")
	public void onCommand(CommandArgs args) {
		if (args.length() != 2) {
			MiscUtils.messagePlayer(args.getSender(), "&cUsage: /ecogive <player> <amount>");
			return;
		}

		Player target = Bukkit.getPlayer(args.getArgs(0));

		if (target == null) {
			MiscUtils.messagePlayer(args.getSender(), args.getArgs(0) + " is not online.");
			return;
		}

		if (!StringUtils.isNumeric(args.getArgs(1))) {
			MiscUtils.messagePlayer(args.getSender(), "&c'" + args.getArgs(1) + "' is not a valid amount.");
			return;
		}

		KitUser targetUser = KitUser.getInstance(target);
		int amount = Integer.parseInt(args.getArgs(1));

		targetUser.setCoins(targetUser.getCoins() + amount);
		targetUser.save();

		if ((args.getSender() instanceof Player) && target == args.getSender()) {
			MiscUtils.messagePlayer(target, "&aYou set your balance to " + targetUser.getCoins() + " coins. &7(+" + amount + ")");
			return;
		}

		MiscUtils.messagePlayer(target, "&aYou were given " + amount + " coins! &7(Total: " + targetUser.getCoins() + ")");
		MiscUtils.messagePlayer(args.getSender(), "&aYou set " + target.getName() + "'s balance to " + targetUser.getCoins() + " coins. &7(+" + amount + ")");
	}
}
