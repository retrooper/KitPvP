package net.foulest.kitpvp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.logging.Logger;

public class MiscUtils {

    public static final Logger LOG = Logger.getLogger("Minecraft");
    public static final Random random = new Random();

    private MiscUtils() {
    }

    public static void messagePlayer(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    public static void broadcastMessage(String message) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(colorize(message));
        }
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
