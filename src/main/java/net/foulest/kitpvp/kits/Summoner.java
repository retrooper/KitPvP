package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.ConfigManager;
import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Summoner implements Kit {

    private final KitPvP kitPvP = KitPvP.getInstance();
    private final ConfigManager config = kitPvP.getConfigFile();

    @Override
    public String getName() {
        return "Summoner";
    }

    @Override
    public ItemStack getDisplayItem() {
        return new ItemStack(Objects.requireNonNull(Material.IRON_BLOCK));
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[0];
    }

    @Override
    public List<ItemStack> getItems() {
        ItemStack sword = new ItemBuilder(Material.WOOD_SWORD).unbreakable(true).build();
        ItemStack special = new ItemBuilder(Material.IRON_BLOCK).name("&aSummon Golem &7(Right Click)")
                .lore("&7Summon your very own Iron Golem.").build();
        return Arrays.asList(sword, special);
    }

    @Override
    public ItemStack[] getArmor() {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner("IG_Spawner");
        skull.setItemMeta(meta);

        return new ItemStack[]{
                new ItemBuilder(skull).name("&fSummoner's Head").unbreakable(true).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).unbreakable(true).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).unbreakable(true).build(),
                new ItemBuilder(Material.CHAINMAIL_BOOTS).unbreakable(true).build()
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Attack: &f4.0",
                "&7Defense: &f5.5",
                "",
                "&7Summon your very own Iron Golem."
        };
    }

    @Override
    public int getCost() {
        return config.getInt("coins.kit-cost");
    }
}
