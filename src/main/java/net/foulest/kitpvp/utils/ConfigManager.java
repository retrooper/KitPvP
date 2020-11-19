package net.foulest.kitpvp.utils;

import net.foulest.kitpvp.KitPvP;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * KitPvP Plugin
 * Manages the plugin's config files.
 *
 * @author Foulest#1955
 */
public class ConfigManager {

	private File file;
	private FileConfiguration config;
	private final KitPvP kitPvP = KitPvP.getInstance();

	public ConfigManager(String fileName, String dir) {
		if (!kitPvP.getDataFolder().exists()) {
			kitPvP.getDataFolder().mkdir();
		}

		if (dir != null && !dir.isEmpty()) {
			File dirFolder = new File("plugins" + File.separator + "KitPvP" + File.separator + dir);

			if (!dirFolder.exists()) {
				dirFolder.mkdir();
			}
		}

		file = new File(kitPvP.getDataFolder(), (dir == null ? fileName + ".yml" : File.separator + dir + File.separator + fileName + ".yml"));

		if (!file.exists()) {
			try {
				if (dir != null) {
					file.createNewFile();
					return;
				}
				attemptFileGrabFromJar(fileName, false);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		config = YamlConfiguration.loadConfiguration(file);
	}

	// Taken from: https://beginnersbook.com/2014/05/how-to-copy-a-file-to-another-file-in-java/
	public static File transferData(ConfigManager copyFrom, ConfigManager copyTo) {
		try {
			FileInputStream inStream = new FileInputStream(copyFrom.file);
			FileOutputStream outStream = new FileOutputStream(copyTo.file);
			byte[] buffer = new byte[1024];
			int length;

			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return copyTo.file;
	}

	public void attemptFileGrabFromJar(String fileName, boolean replace) {
		try {
			kitPvP.saveResource(fileName + ".yml", replace);
		} catch (Exception ignored) {
			MiscUtils.LOG.warning("Unable to grab file from jar.");
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		config = YamlConfiguration.loadConfiguration(this.file);
	}

	public void set(String path, Object value) {
		config.set(path, value);
		save();
	}

	public List<?> getList(String path) {
		return config.getList(path);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String path) {
		return (T) config.get(path);
	}

	public Set<String> getKeys() {
		return config.getKeys(false);
	}

	public Set<String> getKeys(boolean b) {
		return config.getKeys(b);
	}

	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

	public String getString(String path) {
		return config.getString(path);
	}

	public List<Map<?, ?>> getMapList(String path) {
		return config.getMapList(path);
	}

	public ConfigurationSection createSection(String path) {
		ConfigurationSection section = config.createSection(path);
		save();
		return section;
	}

	public ConfigurationSection getSection(String path) {
		return config.getConfigurationSection(path);
	}

	public void save() {
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getBoolean(String string) {
		return config.getBoolean(string);
	}

	public Float getFloat(String string) {
		return ((float) config.getDouble(string));
	}

	public Double getDouble(String string) {
		return config.getDouble(string);
	}

	public int getInt(String string) {
		return config.getInt(string);
	}

	public void addDefault(String path, Object value) {
		config.addDefault(path, value);
		config.options().copyDefaults(true);
		save();
	}

	public void addDefaults(String defaults) {
		kitPvP.saveResource(defaults, false);
	}

	public ItemStack getItemStack(String string) {
		return config.getItemStack(string);
	}

	public Vector getVector(String string) {
		return config.getVector(string);
	}

	public Long getLong(String s) {
		return config.getLong(s);
	}
}
