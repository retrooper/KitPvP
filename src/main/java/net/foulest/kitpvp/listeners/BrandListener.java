package net.foulest.kitpvp.listeners;

import net.foulest.kitpvp.utils.KitUser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

public class BrandListener implements PluginMessageListener {

    private static final BrandListener instance = new BrandListener();

    public static BrandListener getInstance() {
        return instance;
    }

    public void addChannel(Player player, String channel) {
        try {
            player.getClass().getMethod("addChannel", String.class).invoke(player, channel);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] msg) {
        KitUser kitUser = KitUser.getInstance(player);
        kitUser.setClientBrand(new String(msg, StandardCharsets.UTF_8).substring(1));
    }
}