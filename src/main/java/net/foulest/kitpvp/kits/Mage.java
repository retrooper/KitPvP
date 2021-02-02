package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.SkullCreator;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Mage implements Kit {

    @Override
    public String getName() {
        return "Mage";
    }

    @Override
    public int getId() {
        return 11;
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.GLOWSTONE_DUST));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[0];
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack sword = new ItemBuilder(Material.STONE_SWORD).unbreakable(true).build();
        ItemStack special = new ItemBuilder(Material.GLOWSTONE_DUST).name("&aRandom Effect &7(Right Click)")
                .lore("&7Gives you a random effect.").build();
        return Arrays.asList(sword, special);
    }

    @Override
    public ItemStack[] getArmor() {
        String base64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmVl"
                + "MmIxNTQ4NTQ1ZTJhMjQ5N2JkMjRhYWM3OTE3OTI2NTRlZjU4N2E1YWI3M2QzNmFiN2Y1ZDliZjcyYTU0NyJ9fX0=";

        return new ItemStack[]{
                new ItemBuilder(SkullCreator.itemFromBase64(base64)).name("&fMage's Head").unbreakable(true).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).unbreakable(true).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.IRON_BOOTS).unbreakable(true).build()
        };
    }

    @Override
    public String getDescription() {
        return "&7Get random potion effects.";
    }

    @Override
    public double getAttack() {
        return 5.0;
    }

    @Override
    public double getDefense() {
        return 5.0;
    }

    @Override
    public int getCost() {
        return 250;
    }
}
