package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.ConfigManager;
import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Spiderman implements Kit {

    private final KitPvP kitPvP = KitPvP.getInstance();
    private final ConfigManager config = kitPvP.getConfigFile();

    @Override
    public String getName() {
        return "Spiderman";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.WEB));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[0];
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack sword = new ItemBuilder(Material.STONE_SWORD).unbreakable(true).build();
        ItemStack special = new ItemBuilder(Material.WEB).name("&aWeb Slinger &7(Right Click)")
                .lore("&7Traps a player in cobwebs.").build();
        return Arrays.asList(sword, special);
    }

    @Override
    public ItemStack[] getArmor() {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner("zZInteresting");
        skull.setItemMeta(meta);

        return new ItemStack[]{
                new ItemBuilder(skull).name("&fSpiderman's Head").unbreakable(true).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.RED).unbreakable(true).build(),
                new ItemBuilder(Material.DIAMOND_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.DIAMOND_BOOTS).unbreakable(true).build()
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Attack: &f5.0",
                "&7Defense: &f6.0",
                "",
                "&7Trap opponents in spider-webs."
        };
    }

    @Override
    public int getCost() {
        return config.getInt("coins.kit-cost");
    }
}
