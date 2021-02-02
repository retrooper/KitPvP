package net.foulest.kitpvp.utils;

import net.foulest.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class ConfigManager {

    private static File file;
    private static FileConfiguration config;
    private static final KitPvP kitPvP = KitPvP.getInstance();

    private ConfigManager() {
    }

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin(kitPvP.getName()).getDataFolder(), "settings.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
                MiscUtils.LOG.warning("Couldn't create the config file.");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return config;
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException exception) {
            MiscUtils.LOG.warning("Couldn't save the config file.");
        }
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
