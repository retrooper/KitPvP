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
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Hulk implements Kit {

    private final KitPvP kitPvP = KitPvP.getInstance();
    private final ConfigManager config = kitPvP.getConfigFile();

    @Override
    public String getName() {
        return "Hulk";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.PISTON_STICKY_BASE));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[]{
                new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, false, false)
        };
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack sword = new ItemBuilder(Material.STONE_SWORD).unbreakable(true).build();
        ItemStack special = new ItemBuilder(Material.PISTON_STICKY_BASE).name("&aHulk Smash &7(Right Click)")
                .lore("&7Deals nearby players immense knockback.").build();
        return Arrays.asList(sword, special);
    }

    @Override
    public ItemStack[] getArmor() {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner("Hulk");
        skull.setItemMeta(meta);

        return new ItemStack[]{
                new ItemBuilder(skull).name("&fHulk's Head").unbreakable(true).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.fromRGB(0x3E7F2B)).unbreakable(true).build(),
                new ItemBuilder(Material.DIAMOND_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.LEATHER_BOOTS).color(Color.fromRGB(0x3E7F2B)).unbreakable(true).build()
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Attack: &f5.0",
                "&7Defense: &f5.0",
                "",
                "&7Hulk SMASH opponents away."
        };
    }

    @Override
    public int getCost() {
        return config.getInt("coins.kit-cost");
    }
}
