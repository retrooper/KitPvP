package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.ConfigManager;
import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Kangaroo implements Kit {

    private final KitPvP kitPvP = KitPvP.getInstance();
    private final ConfigManager config = kitPvP.getConfigFile();

    @Override
    public String getName() {
        return "Kangaroo";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.FIREWORK));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[]{
                new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, false, false)
        };
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack sword = new ItemBuilder(Material.STONE_SWORD).unbreakable(true).build();
        ItemStack special = new ItemBuilder(Material.FIREWORK).name("&aHop &7(Right Click)")
                .lore("&7Hop into the air. Sneak to go farther.").build();
        return Arrays.asList(sword, special);
    }

    @Override
    public ItemStack[] getArmor() {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner("Kangaroo");
        skull.setItemMeta(meta);

        return new ItemStack[]{
                new ItemBuilder(skull).name("&fKangaroo's Head").unbreakable(true).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.YELLOW).unbreakable(true).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_FALL, 4).unbreakable(true).build()
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Attack: &f5.0",
                "&7Defense: &f5.0",
                "",
                "&7Hop around like a Kangaroo."
        };
    }

    @Override
    public int getCost() {
        return config.getInt("coins.kit-cost");
    }
}
