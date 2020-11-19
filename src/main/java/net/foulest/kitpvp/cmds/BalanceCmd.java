package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BalanceCmd {

	@SuppressWarnings("deprecation")
	@Command(name = "balance", aliases = {"bal", "money", "coins"}, description = "Shows your current balance.", usage = "/balance", inGameOnly = true)
	public void onCommand(CommandArgs args) {
		if (args.length() != 1) {
			MiscUtils.messagePlayer(args.getSender(), "Your Coins: &6" + KitUser.getInstance(args.getPlayer()).getCoins());
			return;
		}

		Player target = Bukkit.getPlayer(args.getArgs(0));
		if (target == null) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args.getArgs(0));

			if (!offlinePlayer.hasPlayedBefore()) {
				MiscUtils.messagePlayer(args.getSender(), "&cPlayer not found.");
				return;
			}

			MiscUtils.messagePlayer(args.getSender(), "" + offlinePlayer.getName() + "'s coins: &6" + KitUser.getInstance(offlinePlayer.getPlayer()).getCoins());
			return;
		}

		MiscUtils.messagePlayer(args.getSender(), "" + target.getName() + "'s coins: &6" + KitUser.getInstance(target).getCoins());
	}
}
