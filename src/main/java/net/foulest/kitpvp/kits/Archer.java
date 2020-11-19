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

public class Archer implements Kit {

	private final KitPvP kitPvP = KitPvP.getInstance();
	private final ConfigManager config = kitPvP.getConfigFile();

	@Override
	public String getName() {
		return "Archer";
	}

	@Override
	public ItemStack getDisplayItem() {
		return new ItemStack(Objects.requireNonNull(Material.BOW));
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[]{
				new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false),
				new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, false, false)
		};
	}

	@Override
	public List<ItemStack> getItems() {
		ItemStack sword = new ItemBuilder(Material.STONE_SWORD).enchant(Enchantment.KNOCKBACK, 1).unbreakable(true).build();
		ItemStack bow = new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 1).unbreakable(true).build();
		ItemStack arrow = new ItemBuilder(Material.ARROW).amount(32).build();
		return Arrays.asList(sword, bow, arrow);
	}

	@Override
	public ItemStack[] getArmor() {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner("Jotaro");
		skull.setItemMeta(meta);

		return new ItemStack[]{
				new ItemBuilder(skull).name("&fArcher's Head").unbreakable(true).build(),
				new ItemBuilder(Material.LEATHER_CHESTPLATE).unbreakable(true).build(),
				new ItemBuilder(Material.LEATHER_LEGGINGS).unbreakable(true).build(),
				new ItemBuilder(Material.LEATHER_BOOTS).enchant(Enchantment.PROTECTION_FALL, 4).unbreakable(true).build()
		};
	}

	@Override
	public String[] getDescription() {
		return new String[]{
				"&7Attack: &f5.0",
				"&7Defense: &f3.0",
				"",
				"&7Master of long-ranged combat."
		};
	}

	@Override
	public int getCost() {
		return config.getInt("coins.kit-cost");
	}
}
