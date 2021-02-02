package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StatsCmd {

    @SuppressWarnings("deprecation")
    @Command(name = "stats", description = "Shows a player's statistics.", usage = "/stats", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player;
        Player sender = args.getPlayer();
        KitUser user;

        if (args.length() > 1) {
            MiscUtils.messagePlayer(sender, "&cUsage: /stats [player]");
            return;
        }

        if (args.length() == 0) {
            user = KitUser.getInstance(sender);

            MiscUtils.messagePlayer(sender, "");
            MiscUtils.messagePlayer(sender, "&e&lYour Stats");
            MiscUtils.messagePlayer(sender, " &7* &fKills: &a" + user.getKills());
            MiscUtils.messagePlayer(sender, " &7* &fDeaths: &a" + user.getDeaths());
            MiscUtils.messagePlayer(sender, " &7* &fK/D Ratio: &a" + user.getKDRText());
            MiscUtils.messagePlayer(sender, " &7* &fKillstreak: &a" + user.getKillstreak());
            MiscUtils.messagePlayer(sender, " &7* &fCoins: &6" + user.getCoins());
            MiscUtils.messagePlayer(sender, "");
        }

        if (args.length() == 1) {
            player = Bukkit.getPlayer(args.getArgs(0));

            if (player == null) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(args.getArgs(0));

                if (op.hasPlayedBefore()) {
                    user = KitUser.getInstance(op.getPlayer());

                    MiscUtils.messagePlayer(sender, "");
                    MiscUtils.messagePlayer(sender, "&e&l" + op.getName() + "'s Stats");
                    MiscUtils.messagePlayer(sender, " &7* &fKills: &a" + user.getKills());
                    MiscUtils.messagePlayer(sender, " &7* &fDeaths: &a" + user.getDeaths());
                    MiscUtils.messagePlayer(sender, " &7* &fK/D Ratio: &a" + user.getKDRText());
                    MiscUtils.messagePlayer(sender, " &7* &fKillstreak: &a" + user.getKillstreak());
                    MiscUtils.messagePlayer(sender, " &7* &fCoins: &6" + user.getCoins());
                    MiscUtils.messagePlayer(sender, "");
                    return;
                }

                MiscUtils.messagePlayer(sender, "&cThis user was not found in the database.");

            } else {
                user = KitUser.getInstance(player);
                MiscUtils.messagePlayer(sender, "");
                MiscUtils.messagePlayer(sender, "&e&l" + player.getName() + "'s Stats");
                MiscUtils.messagePlayer(sender, " &7* &7Kills: &f" + user.getKills());
                MiscUtils.messagePlayer(sender, " &7* &7Deaths: &f" + user.getDeaths());
                MiscUtils.messagePlayer(sender, " &7* &7K/D Ratio: &f" + user.getKDRText());
                MiscUtils.messagePlayer(sender, " &7* &7Killstreak: &f" + user.getKillstreak());
                MiscUtils.messagePlayer(sender, " &7* &7Coins: &f" + user.getCoins());
                MiscUtils.messagePlayer(sender, "");
            }
        }
    }
}
