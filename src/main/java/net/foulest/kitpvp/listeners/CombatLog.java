package net.foulest.kitpvp.listeners;

import net.foulest.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CombatLog {

    private static final CombatLog instance = new CombatLog();
    private final Map<Player, Integer> combatScheduler = new HashMap<>();
    private final Map<Player, Integer> combatHandler = new HashMap<>();
    private final Map<Player, Player> lastAttacker = new HashMap<>();
    private final KitPvP kitPvP = KitPvP.getInstance();

    public static CombatLog getInstance() {
        return instance;
    }

    public void markForCombat(Player damager, Player receiver) {
        if (!isInCombat(damager)) {
            combatHandler.put(damager, 15);
            combatScheduler.put(damager, Bukkit.getScheduler().scheduleSyncRepeatingTask(kitPvP, () -> {
                if (isInCombat(damager)) {
                    if (getRemainingTime(damager) > 1) {
                        combatHandler.replace(damager, getRemainingTime(damager), getRemainingTime(damager) - 1);
                    } else {
                        remove(damager);
                    }
                }
            }, 0L, 20L));
        } else {
            combatHandler.replace(damager, getRemainingTime(damager), 15);
        }

        if (!isInCombat(receiver)) {
            combatHandler.put(receiver, 15);
            combatScheduler.put(receiver, Bukkit.getScheduler().scheduleSyncRepeatingTask(kitPvP, () -> {
                if (isInCombat(receiver)) {
                    if (getRemainingTime(receiver) > 1) {
                        combatHandler.replace(receiver, getRemainingTime(receiver), getRemainingTime(receiver) - 1);
                    } else {
                        remove(receiver);
                    }
                }
            }, 0L, 20L));
        } else {
            combatHandler.replace(receiver, getRemainingTime(receiver), 15);
        }

        lastAttacker.put(receiver, damager);
    }

    public boolean isInCombat(Player player) {
        return combatHandler.containsKey(player);
    }

    public int getRemainingTime(Player player) {
        return !isInCombat(player) ? -1 : combatHandler.get(player);
    }

    public Player getLastAttacker(Player player) {
        return lastAttacker.get(player);
    }

    public void remove(Player player) {
        combatHandler.remove(player);

        if (combatScheduler.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(combatScheduler.get(player));
        }

        combatScheduler.remove(player);
        lastAttacker.remove(player);
    }
}
