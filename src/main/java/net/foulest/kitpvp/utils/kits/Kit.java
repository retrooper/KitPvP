package net.foulest.kitpvp.utils.kits;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.ItemBuilder;
import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public interface Kit {

	String getName();

	ItemStack getDisplayItem();

	List<ItemStack> getItems();

	ItemStack[] getArmor();

	PotionEffect[] getPotionEffects();

	String[] getDescription();

	int getCost();

	default void apply(Player player) {
		KitUser user = KitUser.getInstance(player);

		// Checks if the user owns the kit they're trying to equip.
		if (!user.ownsKit(this)) {
			MiscUtils.messagePlayer(player, String.format("&cYou do not own the " + "%s", getName() + " kit."));
			return;
		}

		// Sets the user's kit data.
		user.setKit(this);

		// Clears the user's inventory and armor.
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);

		// Clears the user's potion effects.
		for (PotionEffect effects : player.getActivePotionEffects()) {
			player.removePotionEffect(effects.getType());
		}

		// Sets the user's armor.
		player.getInventory().setHelmet(getArmor()[0]);
		player.getInventory().setChestplate(getArmor()[1]);
		player.getInventory().setLeggings(getArmor()[2]);
		player.getInventory().setBoots(getArmor()[3]);

		// Sets the user's potion effects.
		if (getPotionEffects() != null) {
			for (PotionEffect effect : getPotionEffects()) {
				if (effect == null) {
					break;
				}

				player.addPotionEffect(effect);
			}
		}

		// Sets the user's soup.
		for (int i = 0; i < player.getInventory().getSize(); ++i) {
			player.getInventory().addItem(new ItemBuilder(Material.MUSHROOM_SOUP).name("&fMushroom Stew").build());
		}

		// Sets the user's kit items.
		for (int i = 0; i < getItems().size(); ++i) {
			player.getInventory().setItem(i, getItems().get(i));
		}

		// Gives a player no fall damage until they take fall damage.
		// After they take it, the metadata is removed.
		// This is necessary when jumping off of a high spawn or using launch pads.
		player.setMetadata("noFall", new FixedMetadataValue(KitPvP.getInstance(), true));

		// Plays a sound, updates the player's inventory, and notifies them.
		player.playSound(player.getLocation(), Sound.SLIME_WALK, 1, 1);
		player.updateInventory();
		MiscUtils.messagePlayer(player, "&aYou equipped the " + getName() + " kit.");
	}
}
