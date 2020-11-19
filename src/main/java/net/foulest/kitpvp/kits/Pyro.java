package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.ConfigManager;
import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Pyro implements Kit {

    private final KitPvP kitPvP = KitPvP.getInstance();
    private final ConfigManager config = kitPvP.getConfigFile();

    @Override
    public String getName() {
        return "Pyro";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.FLINT_AND_STEEL));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[]{
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false)
        };
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack weapon = new ItemBuilder(Material.STONE_SWORD).enchant(Enchantment.FIRE_ASPECT, 1).unbreakable(true).build();
        ItemStack bow = new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 1).enchant(Enchantment.ARROW_FIRE, 1).unbreakable(true).build();
        ItemStack arrow = new ItemBuilder(Material.ARROW).amount(16).build();
        return Arrays.asList(weapon, bow, arrow);
    }

    @Override
    public ItemStack[] getArmor() {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner("MiniMiner_");
        skull.setItemMeta(meta);

        return new ItemStack[]{
                new ItemBuilder(skull).name("&fPyro's Head").unbreakable(true).build(),
                new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).unbreakable(true).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.PROTECTION_FIRE, 4).unbreakable(true).build()
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Attack: &f5.0",
                "&7Defense: &f5.0",
                "",
                "&7Burn your foes alive."
        };
    }

    @Override
    public int getCost() {
        return config.getInt("coins.kit-cost");
    }
}
