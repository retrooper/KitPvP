package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatsCmd {

    @SuppressWarnings("deprecation")
    @Command(name = "stats", description = "Shows a player's statistics.", usage = "/stats", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player;
        Player sender = args.getPlayer();
        KitUser kitUser;

        if (args.length() > 1) {
            MiscUtils.messagePlayer(sender, "&cUsage: /stats [player]");
            return;
        }

        if (args.length() == 0) {
            kitUser = KitUser.getInstance(sender);

            MiscUtils.messagePlayer(sender, "");
            MiscUtils.messagePlayer(sender, " &aYour Stats");
            MiscUtils.messagePlayer(sender, " &fKills: &e" + kitUser.getKills());
            MiscUtils.messagePlayer(sender, " &fDeaths: &e" + kitUser.getDeaths());
            MiscUtils.messagePlayer(sender, " &fK/D Ratio: &e" + kitUser.getKDRText());
            MiscUtils.messagePlayer(sender, "");
            MiscUtils.messagePlayer(sender, " &fStreak: &e" + kitUser.getKillstreak());
            MiscUtils.messagePlayer(sender, " &fHighest Streak: &e" + kitUser.getTopKillstreak());
            MiscUtils.messagePlayer(sender, "");
            MiscUtils.messagePlayer(sender, " &fLevel: &e" + kitUser.getLevel() + " &7(" + kitUser.getExpPercent() + "%)");
            MiscUtils.messagePlayer(sender, " &fCoins: &6" + kitUser.getCoins());
            MiscUtils.messagePlayer(sender, " &fBounty: &cWIP");
            MiscUtils.messagePlayer(sender, "");
            MiscUtils.messagePlayer(sender, " &fEvents Won: &cWIP");
            MiscUtils.messagePlayer(sender, " &fMost Used Kit: &cWIP");
            MiscUtils.messagePlayer(sender, "");
        }

        if (args.length() == 1) {
            if (args.getArgs(0).length() > 16) {
                MiscUtils.messagePlayer(sender, "&cPlayer not found.");
                return;
            }

            player = Bukkit.getPlayer(args.getArgs(0));

            if (player == null) {
                MiscUtils.messagePlayer(sender, "&cPlayer not found.");
                return;
            }

            kitUser = KitUser.getInstance(player);

            MiscUtils.messagePlayer(sender, "");
            MiscUtils.messagePlayer(sender, " &a" + player.getName() + " Stats");
            MiscUtils.messagePlayer(sender, " &fKills: &e" + kitUser.getKills());
            MiscUtils.messagePlayer(sender, " &fDeaths: &e" + kitUser.getDeaths());
            MiscUtils.messagePlayer(sender, " &fK/D Ratio: &e" + kitUser.getKDRText());
            MiscUtils.messagePlayer(sender, "");
            MiscUtils.messagePlayer(sender, " &fStreak: &e" + kitUser.getKillstreak());
            MiscUtils.messagePlayer(sender, " &fHighest Streak: &e" + kitUser.getTopKillstreak());
            MiscUtils.messagePlayer(sender, "");
            MiscUtils.messagePlayer(sender, " &fLevel: &e" + kitUser.getLevel() + " &7(" + kitUser.getExpPercent() + "%)");
            MiscUtils.messagePlayer(sender, " &fCoins: &6" + kitUser.getCoins());
            MiscUtils.messagePlayer(sender, " &fBounty: &cWIP");
            MiscUtils.messagePlayer(sender, "");
            MiscUtils.messagePlayer(sender, " &fEvents Won: &cWIP");
            MiscUtils.messagePlayer(sender, " &fMost Used Kit: &cWIP");
            MiscUtils.messagePlayer(sender, "");
        }
    }
}
