package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.SkullCreator;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Fisherman implements Kit {

    @Override
    public String getName() {
        return "Fisherman";
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.FISHING_ROD));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[0];
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack sword = new ItemBuilder(Material.STONE_SWORD).unbreakable(true).build();
        ItemStack special = new ItemBuilder(Material.FISHING_ROD).name("&aHookshot &7(Right Click)")
                .lore("&7Hooks players to your location.").unbreakable(true).build();
        return Arrays.asList(sword, special);
    }

    @Override
    public ItemStack[] getArmor() {
        String base64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv"
                + "MTcxNTI4NzZiYzNhOTZkZDJhMjI5OTI0NWVkYjNiZWVmNjQ3YzhhNTZhYzg4NTNhNjg3YzNlN2I1ZDhiYiJ9fX0=";

        return new ItemStack[]{
                new ItemBuilder(SkullCreator.itemFromBase64(base64)).name("&fFisherman's Head").unbreakable(true).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.fromRGB(0xBF8426)).unbreakable(true).build(),
                new ItemBuilder(Material.IRON_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.IRON_BOOTS).unbreakable(true).build()
        };
    }

    @Override
    public String getDescription() {
        return "&7Hooks players to your location.";
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
