package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PayCmd {

    @Command(name = "pay", description = "Send coins to another player.", usage = "/pay <player> <amount>", permission = "kitpvp.pay", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player target = Bukkit.getPlayer(args.getArgs(0));

        if (args.length() == 2) {
            if (target == null) {
                MiscUtils.messagePlayer(args.getSender(), args.getArgs(0) + " is not online.");
                return;
            }

            String amount = args.getArgs(1);

            if (!StringUtils.isNumeric(amount)) {
                MiscUtils.messagePlayer(args.getSender(), "&c'" + amount + "' is not a valid amount.");
                return;
            }

            if (amount.contains("-")) {
                MiscUtils.messagePlayer(args.getPlayer(), "&cThe amount must be positive.");
                return;
            }

            Player player = args.getPlayer();
            Player sender = (Player) args.getSender();
            KitUser targetUser = KitUser.getInstance(target);
            KitUser senderUser = KitUser.getInstance(sender);
            int oldAmount = targetUser.getCoins();
            int check = senderUser.getCoins() - Integer.parseInt(amount);

            if (check <= 0) {
                MiscUtils.messagePlayer(senderUser.getPlayer(), "&cYou don't have enough coins.");
                return;
            }

            targetUser.setCoins((Integer.parseInt(amount) + oldAmount));
            senderUser.removeCoins(Integer.parseInt(amount));
            targetUser.save();

            if ((args.getSender() instanceof Player) && target == args.getSender()) {
                MiscUtils.messagePlayer(target, "&cYou can't pay yourself.");
                return;
            }

            MiscUtils.messagePlayer(target, "&a" + player.getName() + " sent you " + amount + " coins!");
            MiscUtils.messagePlayer(args.getSender(), "&aYou sent " + target.getName() + " " + amount + " coins!");
            return;
        }

        MiscUtils.messagePlayer(args.getSender(), "&cUsage: /pay <player> <amount>");
    }
}
