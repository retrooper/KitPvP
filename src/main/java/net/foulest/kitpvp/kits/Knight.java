package net.foulest.kitpvp.kits;

import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.kits.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Knight implements Kit {

	@Override
	public String getName() {
		return "Knight";
	}

	@Override
	public ItemStack getDisplayItem() {
		return new ItemStack(Objects.requireNonNull(Material.IRON_CHESTPLATE));
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[0];
	}

	@Override
	public List<ItemStack> getItems() {
		ItemStack sword = new ItemBuilder(Material.STONE_SWORD).unbreakable(true).build();
		return Collections.singletonList(sword);
	}

	@Override
	public ItemStack[] getArmor() {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner("KnightDawner");
		skull.setItemMeta(meta);

		return new ItemStack[]{
				new ItemBuilder(skull).name("&fKnight's Head").unbreakable(true).build(),
				new ItemBuilder(Material.IRON_CHESTPLATE).unbreakable(true).build(),
				new ItemBuilder(Material.IRON_LEGGINGS).unbreakable(true).build(),
				new ItemBuilder(Material.IRON_BOOTS).unbreakable(true).build()
		};
	}

	@Override
	public String[] getDescription() {
		return new String[]{
				"&7Attack: &f5.0",
				"&7Defense: &f6.5",
				"",
				"&7No perks or abilities."
		};
	}

	@Override
	public int getCost() {
		return 0;
	}
}
