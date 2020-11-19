package net.foulest.kitpvp.utils;

import net.foulest.kitpvp.utils.kits.Kit;
import net.foulest.kitpvp.utils.kits.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitSelector {

    private final Inventory inv;
    private final KitManager kitManager = KitManager.getInstance();
    private static final Map<Player, Integer> page = new HashMap<>();
	public static final String INVENTORY_NAME = MiscUtils.colorize("Kit Selector");

    public KitSelector(Player player) {
        inv = Bukkit.createInventory(player, ensureSize(kitManager.getKits().size()) + 18, INVENTORY_NAME);

        populateInventory(player, 0);
        player.closeInventory();
        player.openInventory(inv);
        page.put(player, 0);
    }

    public KitSelector(Player player, int page) {
        inv = Bukkit.createInventory(player, ensureSize(kitManager.getKits().size()) + 18, INVENTORY_NAME + " - Page: " + (page + 1));

        populateInventory(player, page);
        player.closeInventory();
        player.openInventory(inv);
        KitSelector.page.put(player, page);
    }

    public static int getPage(Player player) {
        return page.get(player);
    }

    // Ensures that we use enough slots to hold all the kit items.
    private int ensureSize(int size) {
        if (size >= 36) {
			return 36;
		}

        if ((size + 18) % 9 == 0) {
			return size;
		}

        return ensureSize(++size);
    }

    // Populates the GUI's inventory.
    private void populateInventory(Player player, int page) {
        KitUser user = KitUser.getInstance(player);
        ItemStack glass = new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name(" ").build();

        // Sets non-present items to glass.
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, glass);
        }
        for (int i = (inv.getSize() - 9); i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        if (page > 0) {
            inv.setItem(inv.getSize() - 9, new ItemBuilder(Material.BOOK).name("&aPrevious Page").build());
        }

        List<Kit> checkedKits = kitManager.getKits().subList(page * 36, (page * 36) + ensureKits(kitManager.getKits().size() - (page * 36)));

        try {
            List<Kit> futureCheck = kitManager.getKits().subList((page + 1) * 36, ((page + 1) * 36) + ensureKits(kitManager.getKits().size() - ((page + 1) * 36)));
            if (!futureCheck.isEmpty()) {
                inv.setItem(inv.getSize() - 1, new ItemBuilder(Material.BOOK).name("&aNext Page").build());
            }
        } catch (IllegalArgumentException ignored) {
        }

        for (Kit kits : checkedKits) {
            if (user.ownsKit(kits)) {
                inv.addItem(createKitItem(kits));
            }
        }
    }

    public Inventory getInventory() {
        return inv;
    }

    private int ensureKits(int size) {
        return (Math.min(size, 36));
    }

    private ItemStack createKitItem(Kit kit) {
        List<String> items = new ArrayList<>(Arrays.asList(kit.getDescription()));
        items.add("");
        items.add("&aClick to equip this kit.");
        return new ItemBuilder(kit.getDisplayItem()).name("&a" + kit.getName()).lore(items).build();
    }
}
