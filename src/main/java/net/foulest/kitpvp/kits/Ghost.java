package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Ghost implements Kit {

    @Override
    public String getName() {
        return "Ghost";
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.GHAST_TEAR));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[]{
                new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true)
        };
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).unbreakable(true).build();
        ItemStack air = new ItemBuilder(Material.AIR).build();
        return Arrays.asList(sword, air);
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.AIR).build(),
                new ItemBuilder(Material.AIR).build(),
                new ItemBuilder(Material.AIR).build(),
                new ItemBuilder(Material.AIR).build()
        };
    }

    @Override
    public String getDescription() {
        return "&7Permanent invisibility.";
    }

    @Override
    public double getAttack() {
        return 9.5;
    }

    @Override
    public double getDefense() {
        return 0.0;
    }

    @Override
    public int getCost() {
        return 250;
    }
}
