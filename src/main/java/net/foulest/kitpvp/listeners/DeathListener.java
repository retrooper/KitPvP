package net.foulest.kitpvp.listeners;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.*;
import net.foulest.kitpvp.utils.kits.Kit;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.List;

public class DeathListener implements Listener {

    public static void handleDeath(Player player) {
        KitPvP kitPvP = KitPvP.getInstance();
        ConfigManager config = kitPvP.getConfigFile();
        Spawn spawn = Spawn.getInstance();
        CombatLog combatLog = CombatLog.getInstance();
        KitUser receiver = KitUser.getInstance(player);
        Kit currentKit = receiver.getKit();
        Vector vec = new Vector();

        // On-death blood splatter effect.
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

        // Sets the player's current kit and adds a death.
        receiver.setPreviousKit(currentKit);
        receiver.addDeath();
        MiscUtils.sendActionbar(player, "&c&lYOU DIED! &7Your kit has been reset.");

        // Runs specific code if the player is killed by another player.
        if (combatLog.getLastAttacker(player) != null) {
            KitUser damager = KitUser.getInstance(combatLog.getLastAttacker(player));
            int rewardAmount = config.getInt("coins.kill-bonus");
            int inventorySize = damager.getPlayer().getInventory().getSize();

            // Adds a kill to the damager.
            damager.addKill();
            damager.addKillstreak();
            damager.getPlayer().playSound(damager.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 0.5f, 0.0f);

            // Sends all online players a killstreak message in chat.
            // Gives the damager coins and refills their inventory with soup.
            if (damager.getKillstreak() >= 5 && damager.getKillstreak() % 5 == 0) {
                MiscUtils.broadcastMessage("&e&lSTREAK! &f" + damager.getPlayer().getName() + " &7is on a &f" + damager.getKillstreak() + " &7killstreak!");

                rewardAmount += damager.getKillstreak();

                for (int i = 0; i < inventorySize; ++i) {
                    damager.getPlayer().getInventory().addItem(new ItemBuilder(Material.MUSHROOM_SOUP).name("&fMushroom Soup").build());
                }

                List<ItemStack> kitItems = damager.getKit().getItems();
                for (int i = 0; i < kitItems.size(); ++i) {
                    damager.getPlayer().getInventory().setItem(i, kitItems.get(i));
                }
            }

            damager.addCoins(rewardAmount);
            MiscUtils.sendActionbar(damager.getPlayer(), "&a&lKILL! &7You killed &f" + receiver.getPlayer().getName() + "&7. (+" + rewardAmount + " coins)");
        }

        // Sends all online players a killstreak message in chat.
        if (receiver.getKillstreak() >= 5) {
            MiscUtils.broadcastMessage("&e&lSTREAK! &f" + receiver.getPlayer().getName() + " &7lost their killstreak of &f" + receiver.getKillstreak() + "&7.");
        }

        // Removes the player's combat tag.
        combatLog.remove(player);

        // Removes knockback before teleporting the player to spawn.
        Bukkit.getScheduler().runTaskLater(kitPvP, () -> player.setVelocity(vec), 1L);

        // Prevents the player from reaching the respawn screen using NMS.
        Bukkit.getScheduler().runTask(kitPvP, () -> {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            PacketPlayInClientCommand packet = new PacketPlayInClientCommand();

            try {
                Field a = PacketPlayInClientCommand.class.getDeclaredField("a");
                a.setAccessible(true);
                a.set(packet, PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
            } catch (Exception e) {
                e.printStackTrace();
            }

            (craftPlayer.getHandle()).playerConnection.a(packet);
        });

        // Teleports the player to spawn.
        spawn.teleport(player);
        player.playSound(player.getLocation(), Sound.FALL_BIG, 0.5f, 0.0f);

        // Resets the player's killstreak.
        receiver.resetKillStreak();
    }
}
