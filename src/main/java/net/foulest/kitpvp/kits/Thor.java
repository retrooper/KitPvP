package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.ConfigManager;
import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Thor implements Kit {

    private final KitPvP kitPvP = KitPvP.getInstance();
    private final ConfigManager config = kitPvP.getConfigFile();

    @Override
    public String getName() {
        return "Thor";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.IRON_AXE));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[0];
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack sword = new ItemBuilder(Material.IRON_AXE).name("&aMjolnir &7(Right Click)")
                .lore("&7Strikes nearby players with lightning.").unbreakable(true).build();
        return Collections.singletonList(sword);
    }

    @Override
    public ItemStack[] getArmor() {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner("Thor71");
        skull.setItemMeta(meta);

        return new ItemStack[]{
                new ItemBuilder(skull).name("&fThor's Head").unbreakable(true).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).unbreakable(true).build(),
                new ItemBuilder(Material.GOLD_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.GOLD_BOOTS).unbreakable(true).build()
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Attack: &f5.0",
                "&7Defense: &f5.0",
                "",
                "&7Strike lightning on your enemies."
        };
    }

    @Override
    public int getCost() {
        return config.getInt("coins.kit-cost");
    }
}
