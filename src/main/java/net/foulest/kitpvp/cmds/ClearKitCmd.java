package net.foulest.kitpvp.cmds;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.listeners.CombatLog;
import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.command.Command;
import net.foulest.kitpvp.utils.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class ClearKitCmd {

    private final CombatLog combatLog = CombatLog.getInstance();
    private final KitPvP kitPvP = KitPvP.getInstance();

    @Command(name = "clearkit", description = "Clears your kit.", usage = "/clearkit", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        if (!(args.getSender() instanceof Player)) {
            MiscUtils.messagePlayer(args.getSender(), "Only players can execute this command.");
            return;
        }

        Player sender = args.getPlayer();
        KitUser kitUser = KitUser.getInstance(sender);

        // Clearing your own kit.
        if (args.length() == 0) {
            if (combatLog.isInCombat(args.getPlayer())) {
                MiscUtils.messagePlayer(args.getPlayer(), "&cYou may not use this command while in combat.");
                return;
            }

            if (kitUser.isInSafezone()) {
                if (!kitUser.hasKit()) {
                    MiscUtils.messagePlayer(sender, "&cYou do not have a kit selected.");
                    return;
                }

                clearKit(kitUser);
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

            clearKit(targetData);
            MiscUtils.messagePlayer(target, "&aYour kit has been cleared by a staff member.");
            MiscUtils.messagePlayer(sender, "&aYou cleared " + target.getName() + "'s kit.");
        }
    }

    public void clearKit(KitUser user) {
        Player player = user.getPlayer();

        user.setPreviousKit(user.getKit());
        user.clearCooldowns();
        user.setKit(null);

        player.setHealth(20);
        player.getInventory().setHeldItemSlot(0);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        kitPvP.giveDefaultItems(player);

        player.playSound(player.getLocation(), Sound.SLIME_WALK, 1, 1);
    }
}
