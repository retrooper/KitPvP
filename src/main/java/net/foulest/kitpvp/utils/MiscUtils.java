package net.foulest.kitpvp.utils;

import net.foulest.kitpvp.KitPvP;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

	public static void sendActionbar(Player player, String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\""
				+ message.replace("&", "§") + "\"}"), (byte) 2);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public static void broadcastActionbar(String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\""
				+ message.replace("&", "§") + "\"}"), (byte) 2);

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static void sendPlayer(Player player, String server) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		Bukkit.getPlayer(player.getUniqueId()).sendPluginMessage(KitPvP.getInstance(), "BungeeCord", b.toByteArray());
	}
}
