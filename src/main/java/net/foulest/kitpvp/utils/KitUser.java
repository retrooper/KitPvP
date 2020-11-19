package net.foulest.kitpvp.utils;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.kits.Kit;
import net.foulest.kitpvp.utils.kits.KitManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class KitUser {

    static final Set<KitUser> instances = new HashSet<>();
    private final Player player;
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final KitPvP kitPvP = KitPvP.getInstance();
    private final KitManager kitManager = KitManager.getInstance();
    private final Set<Kit> ownedKits = new HashSet<>();
    private BukkitTask abilityCooldownNotifier;
    private ConfigManager file;
    private Kit kit;
    private Kit previousKit;
    private int coins;
    private int kills;
    private int deaths;
    private int killstreak;

    private KitUser(Player player) {
        this.player = player;
        this.kit = null;
        instances.add(this);
    }

    public static KitUser getInstance(Player player) {
        for (KitUser users : instances) {
            if (users != null && users.getPlayer() != null && users.getPlayer().isOnline()
                    && users.getPlayer().getName().equalsIgnoreCase(player.getName())) {
                return users;
            }
        }

        return new KitUser(player);
    }

    public Player getPlayer() {
        return player;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public boolean hasKit() {
        return kit != null;
    }

    public Kit getPreviousKit() {
        return previousKit;
    }

    public void setPreviousKit(Kit kit) {
        this.previousKit = kit;
    }

    public boolean hasPreviousKit() {
        return previousKit != null;
    }

    public boolean ownsKit(Kit kit) {
        return ownedKits.contains(kit);
    }

    public void addOwnedKit(Kit kit) {
        ownedKits.add(kit);
    }

    public Set<Kit> getKits() {
        return ownedKits;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void removeCoins(int coins) {
        this.coins = Math.max(this.coins - coins, 0);
    }

    public boolean hasCooldown(Player player, String kit) {
        long cooldown = cooldowns.containsKey(kit) ? (cooldowns.get(kit) - System.currentTimeMillis()) : 0L;

        if (cooldown > 0) {
            MiscUtils.messagePlayer(player, "&cYou are still on cooldown for %time% seconds.".replace("%time%",
                    String.valueOf(BigDecimal.valueOf((double) cooldown / 1000)
                            .setScale(1, RoundingMode.HALF_UP).doubleValue())));
            return true;
        }

        return false;
    }

    public long getCooldown(String kit) {
        return cooldowns.containsKey(kit) ? (cooldowns.get(kit) - System.currentTimeMillis()) : 0L;
    }

    public void clearCooldowns() {
        cooldowns.clear();

        if (abilityCooldownNotifier != null) {
            abilityCooldownNotifier.cancel();
            abilityCooldownNotifier = null;
        }
    }

    public void setCooldown(String kitName, int cooldownTime, boolean notify) {
        cooldowns.put(kitName, System.currentTimeMillis() + cooldownTime * 1000);

        if (notify) {
            abilityCooldownNotifier = new BukkitRunnable() {
                public void run() {
                    if (player != null) {
                        MiscUtils.messagePlayer(player, MiscUtils.colorize("&aYour ability cooldown has expired."));
                    }
                }
            }.runTaskLater(kitPvP, cooldownTime * 20L);
        }
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void load() throws IOException {
        KitUser kitUser = KitUser.getInstance(player);
        file = new ConfigManager(player.getUniqueId().toString(), "players");

        if (!file.getFile().exists()) {
            kitUser.save();
        }

        BufferedReader reader = new BufferedReader(new FileReader(file.getFile()));

        if (reader.readLine() == null) {
            file.setFile(ConfigManager.transferData(kitPvP.getDefaultPlayerFile(), file));
            reader.close();
        }

        if (!file.getList("kits").isEmpty()) {
            for (String kits : file.getStringList("kits")) {
                ownedKits.add(kitManager.valueOf(kits));
            }
        }

        setCoins(file.getInt("coins"));
        setKills(file.getInt("kills"));
        setDeaths(file.getInt("deaths"));
        setKillstreak(file.getInt("killstreak"));
    }

    public void save() {
        if (!ownedKits.isEmpty()) {
            List<String> kitList = new ArrayList<>();

            for (Kit kits : ownedKits) {
                if (kits == null) {
                    continue;
                }

                kitList.add(kits.getName());
            }

            file.set("kits", kitList);
        }

        file.set("coins", getCoins());
        file.set("kills", getKills());
        file.set("deaths", getDeaths());
        file.set("killstreak", getKillstreak());
    }

    public void unload() {
        ownedKits.clear();
        kit = null;
        previousKit = null;
        file = null;
        instances.remove(this);
    }

    public void addDeath() {
        deaths += 1;
    }

    public void addKill() {
        kills += 1;
    }

    public int getKillstreak() {
        return killstreak;
    }

    public void setKillstreak(int killstreak) {
        this.killstreak = killstreak;
    }

    public void addKillstreak() {
        killstreak += 1;
    }

    public void resetKillStreak() {
        killstreak = 0;
    }

    public double getKDR() {
        return (getDeaths() == 0) ? getKills() : (double) getKills() / (double) getDeaths();
    }

    public String getKDRText() {
        return new DecimalFormat("#.##").format(getKDR());
    }
}
