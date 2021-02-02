package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCmd {

    @SuppressWarnings("deprecation")
    @Command(name = "balance", aliases = {"bal", "money", "coins"}, description = "Shows your current balance.", usage = "/balance", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();
        CommandSender sender = args.getSender();

        if (args.length() != 1) {
            MiscUtils.messagePlayer(sender, "Your Coins: &6" + KitUser.getInstance(player).getCoins());
            return;
        }

        Player target = Bukkit.getPlayer(args.getArgs(0));

        if (target == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args.getArgs(0));

            if (!offlinePlayer.hasPlayedBefore()) {
                MiscUtils.messagePlayer(sender, "&cPlayer not found.");
                return;
            }

            MiscUtils.messagePlayer(sender, "" + offlinePlayer.getName() + "'s Coins: &6" + KitUser.getInstance(offlinePlayer.getPlayer()).getCoins());
            return;
        }

        MiscUtils.messagePlayer(args.getSender(), "" + target.getName() + "'s Coins: &6" + KitUser.getInstance(target).getCoins());
    }
}
