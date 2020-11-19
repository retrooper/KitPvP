package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.ConfigManager;
import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Tank implements Kit {

    private final KitPvP kitPvP = KitPvP.getInstance();
    private final ConfigManager config = kitPvP.getConfigFile();

    @Override
    public String getName() {
        return "Tank";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.DIAMOND_CHESTPLATE));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[]{
                new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false, false)
        };
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack weapon = new ItemBuilder(Material.STONE_AXE).unbreakable(true).build();
        return Collections.singletonList(weapon);
    }

    @Override
    public ItemStack[] getArmor() {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner("KingPiglin");
        skull.setItemMeta(meta);

        return new ItemStack[]{
                new ItemBuilder(skull).name("&fTank's Head").unbreakable(true).build(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).unbreakable(true).build(),
                new ItemBuilder(Material.DIAMOND_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.DIAMOND_BOOTS).unbreakable(true).build()
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Attack: &f4.0",
                "&7Defense: &f9.0",
                "",
                "&7Slow but very resistant.",
        };
    }

    @Override
    public int getCost() {
        return config.getInt("coins.kit-cost");
    }
}
