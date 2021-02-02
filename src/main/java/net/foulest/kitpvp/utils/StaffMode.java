package net.foulest.kitpvp.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StaffMode {

    private static final StaffMode instance = new StaffMode();
    private final Spawn spawn = Spawn.getInstance();

    public static StaffMode getInstance() {
        return instance;
    }

    public void toggleStaffMode(Player player, boolean status, boolean silent) {
        KitUser kitUser = KitUser.getInstance(player);

        if (kitUser.isInStaffMode() == status) {
            MiscUtils.messagePlayer(player, "&cIt would have no effect.");
            return;
        }

        // Changes staff mode status in the KitUser class.
        kitUser.setStaffMode(status);

        // Changes vanish status, gamemode, and inventory items.
        if (kitUser.isInStaffMode()) {
            if (!silent) {
                MiscUtils.messagePlayer(player, "&aStaff mode has been enabled.");
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("kitpvp.staff")) {
                    p.hidePlayer(player);
                }
            }

            player.setGameMode(GameMode.CREATIVE);

            kitUser.setKit(null);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
            player.updateInventory();

            ItemStack randomTeleport = new ItemBuilder(Material.COMPASS).name("&aRandom Teleport &7(Right Click)").build();
            player.getInventory().setItem(0, randomTeleport);

            ItemStack exitStaffMode = new ItemBuilder(Material.BED).name("&cExit Staff Mode &7(Right Click)").build();
            player.getInventory().setItem(8, exitStaffMode);

        } else {
            if (!silent) {
                MiscUtils.messagePlayer(player, "&cStaff mode has been disabled.");
            }

            spawn.teleport(player);
            player.setGameMode(GameMode.ADVENTURE);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(player);
            }
        }
    }
}
