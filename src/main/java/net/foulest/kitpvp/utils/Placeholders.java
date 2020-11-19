package net.foulest.kitpvp.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.foulest.kitpvp.listeners.CombatLog;
import org.bukkit.entity.Player;

public class Placeholders extends PlaceholderExpansion {

    private final CombatLog combatLog = CombatLog.getInstance();

    @Override
    public String getIdentifier() {
        return "kitpvp";
    }

    @Override
    public String getAuthor() {
        return "Foulest";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        // Placeholder: %kitpvp_kills%
        if (identifier.equals("kills")) {
            if (player == null) {
                return "0";
            }
            KitUser user = KitUser.getInstance(player);
            return String.valueOf(user.getKills());
        }

        // Placeholder: %kitpvp_deaths%
        if (identifier.equals("deaths")) {
            if (player == null) {
                return "0";
            }
            KitUser user = KitUser.getInstance(player);
            return String.valueOf(user.getDeaths());
        }

        // Placeholder: %kitpvp_kdr%
        if (identifier.equals("kdr")) {
            if (player == null) {
                return "0";
            }
            KitUser user = KitUser.getInstance(player);
            return String.valueOf(user.getKDRText());
        }

        // Placeholder: %kitpvp_killstreak%
        if (identifier.equals("killstreak")) {
            if (player == null) {
                return "0";
            }
            KitUser user = KitUser.getInstance(player);
            return String.valueOf(user.getKillstreak());
        }

        // Placeholder: %kitpvp_coins%
        if (identifier.equals("coins")) {
            if (player == null) {
                return "0";
            }
            KitUser user = KitUser.getInstance(player);
            return String.valueOf(user.getCoins());
        }

        // Placeholder: %kitpvp_combattag%
        if (identifier.equals("combattag")) {
            return combatLog.isInCombat(player) ? "&cFighting" : "&aSafe";
        }

        // Placeholder: %kitpvp_activekit%
        if (identifier.equals("activekit")) {
            if (player == null) {
                return "None";
            }
            KitUser user = KitUser.getInstance(player);
            if (user.getKit() == null) {
                return "None";
            }
            return String.valueOf(user.getKit().getName());
        }

        return null;
    }
}
