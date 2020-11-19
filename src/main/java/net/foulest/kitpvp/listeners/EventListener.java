package net.foulest.kitpvp.listeners;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.*;
import net.foulest.kitpvp.utils.kits.Kit;
import net.foulest.kitpvp.utils.kits.KitManager;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class EventListener implements Listener {

    private final Spawn spawn = Spawn.getInstance();
    private final KitPvP kitPvP = KitPvP.getInstance();
    private final EndSpawn endSpawn = EndSpawn.getInstance();
    private final KitManager kitManager = KitManager.getInstance();
    private final ConfigManager config = kitPvP.getConfigFile();
    private final CombatLog combatLog = CombatLog.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();
        KitUser user = KitUser.getInstance(player);

        user.load();

        for (Kit kit : kitManager.getKits()) {
            if (kit.getCost() == 0.0 && !user.ownsKit(kit)) {
                user.addOwnedKit(kit);
            }
        }

        spawn.teleport(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        KitUser user = KitUser.getInstance(player);

        if (player.hasMetadata("noFall")) {
            player.removeMetadata("noFall", kitPvP);
        }

        kitPvP.spawnTimers.remove(player.getUniqueId());

        RegionUtil.PLAYERS_IN_REGIONS.remove(player.getUniqueId());

        if (combatLog.isInCombat(player)) {
            if (combatLog.getLastAttacker(player) != null) {
                KitUser killer = KitUser.getInstance(combatLog.getLastAttacker(player));

                killer.getPlayer().playSound(killer.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 0.5f, 0.0f);
                killer.addKill();
                killer.addKillstreak();
                MiscUtils.sendActionbar(killer.getPlayer(), "&a&lKILL! &7You killed &f" + player.getName() + "&7.");
            }

            combatLog.remove(player);
            user.addDeath();
        }

        user.resetKillStreak();
        user.save();
        user.unload();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        event.getDrops().clear();
        event.setDroppedExp(0);

        RegionUtil.PLAYERS_IN_REGIONS.remove(player.getUniqueId());

        DeathListener.handleDeath(player);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (event.getMessage().startsWith("/") && kitManager.valueOf(event.getMessage().substring(0, 1)) != null) {
            kitManager.valueOf(event.getMessage().substring(0, 1)).apply(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (event.getItemDrop().getItemStack().getType() == Material.BOWL) {
                event.getItemDrop().remove();
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (RegionUtil.isInSafezone(player, player.getLocation())) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowShoot(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
            Arrow arrow = (Arrow) event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                Player damager = (Player) arrow.getShooter();
                Player receiver = (Player) event.getEntity();

                if (receiver == damager) {
                    event.setCancelled(true);
                    return;
                }

                combatLog.markForCombat(damager, receiver);
                MiscUtils.messagePlayer(damager.getPlayer(), "&c" + receiver.getPlayer().getName()
                        + " &eis on &c" + String.format("%.01f", Math.max(receiver.getHealth() - event.getFinalDamage(), 0.0)) + " &ehealth.");

                new BukkitRunnable() {
                    public void run() {
                        ((CraftPlayer) receiver).getHandle().getDataWatcher().watch(9, (byte) 0);
                    }
                }.runTaskLater(kitPvP, 100);
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0);

        if (event.getEntity().getType() != EntityType.ENDERMAN) {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Wolf) {
                Wolf wolf = (Wolf) event.getEntity();

                if (wolf.getOwner() == event.getDamager()) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (event.getEntity() instanceof Player) {
                Player damager = (Player) event.getDamager();
                Player receiver = (Player) event.getEntity();

                combatLog.markForCombat(damager, receiver);
            }

            if (event.getEntity() instanceof IronGolem && event.getEntity().hasMetadata(event.getDamager().getName())) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getDamager() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getDamager();

            if (wolf.getOwner() != null) {
                Player damager = (Player) wolf.getOwner();
                Player receiver = (Player) event.getEntity();

                combatLog.markForCombat(damager, receiver);
                return;
            }
        }

        if (event.getDamager() instanceof IronGolem) {
            IronGolem ironGolem = (IronGolem) event.getDamager();
            Player receiver = (Player) event.getEntity();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (ironGolem.hasMetadata(p.getName())) {
                    combatLog.markForCombat(p, receiver);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (kitPvP.spawnTimers.containsKey(player.getUniqueId())) {
                kitPvP.spawnTimers.remove(player.getUniqueId());
                MiscUtils.sendActionbar(player, MiscUtils.colorize("&cTeleportation cancelled, you took damage."));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        KitUser user = KitUser.getInstance(player);

        if (!user.hasKit() && event.getInventory().getType() == InventoryType.PLAYER
                && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getWhoClicked() instanceof Player) || event.getCurrentItem() == null
                || !event.getCurrentItem().hasItemMeta() || event.getCurrentItem().getItemMeta() == null) {
            return;
        }

        if (user.getKit() != null && event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
            return;
        }

        // Kit Shop
        if (event.getView().getTitle().contains("Kit Shop")) {
            event.setCancelled(true);

            if (kitManager.valueOf(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) != null) {
                Kit kit = kitManager.valueOf(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

                if ((KitUser.getInstance(player).getCoins() - kit.getCost()) < 0) {
                    MiscUtils.messagePlayer(player, "&cYou do not have enough coins to purchase " + kit.getName() + ".");
                    return;
                }

                KitUser.getInstance(player).addOwnedKit(kit);
                KitUser.getInstance(player).removeCoins(kit.getCost());
                MiscUtils.messagePlayer(player, "&aYou purchased the " + kit.getName() + " kit for " + kit.getCost() + " coins.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                player.closeInventory();
                return;
            }
        }

        // Kit Selector
        if (event.getView().getTitle().contains("Kit Selector")) {
            event.setCancelled(true);

            if (kitManager.valueOf(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) == null) {
                switch (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase())) {
                    case "-->":
                        new KitSelector(player, KitSelector.getPage(player) + 1);
                        break;

                    case "<--":
                        new KitSelector(player, KitSelector.getPage(player) - 1);
                        break;

                    case "kit shop":
                        new KitShop(player);
                        break;

                    default:
                        break;
                }
                return;
            }

            kitManager.valueOf(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())).apply(player);
            player.updateInventory();
            player.closeInventory();
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        event.setFoodLevel(20);
        player.setSaturation(20);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (!player.getGameMode().equals(GameMode.CREATIVE)
                && event.getItem().getItemStack().getType() != Material.ENDER_PEARL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();

        if (!KitUser.getInstance(player).hasKit() && !player.getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason spawnEvent = event.getSpawnReason();

        if (spawnEvent != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG
                && spawnEvent != CreatureSpawnEvent.SpawnReason.SPAWNER
                && spawnEvent != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        World world = event.getFrom();

        if (world.getPlayers().isEmpty()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.PLAYER && entity.getType() != EntityType.ARMOR_STAND) {
                    entity.remove();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction().toString().contains("RIGHT") && item != null && player.getGameMode() != GameMode.CREATIVE) {
            if (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null
                    && item.getType() != Material.FISHING_ROD && item.getType() != Material.SNOW_BALL
                    && item.getType() != Material.WEB && item.getType() != Material.DISPENSER
                    && item.getType() != Material.BLAZE_ROD && item.getType() != Material.IRON_BLOCK
                    && item.getItemMeta().getDisplayName().toLowerCase().contains("right click")) {
                event.setCancelled(true);
            }

            switch (item.getType()) {
                case MUSHROOM_SOUP:
                    if (player.getHealth() < player.getMaxHealth()) {
                        event.setCancelled(true);
                        player.setHealth(Math.min(player.getHealth() + 7, player.getMaxHealth()));
                        player.setItemInHand(new ItemBuilder(Material.BOWL).name("&fBowl").build());
                    }
                    break;

                case WATCH:
                    if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Previous Kit")
                            && KitUser.getInstance(player).hasPreviousKit() && !KitUser.getInstance(player).hasKit()) {
                        event.setCancelled(true);
                        KitUser.getInstance(player).getPreviousKit().apply(player);
                    }
                    break;

                case NETHER_STAR:
                    if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Kit Selector")) {
                        event.setCancelled(true);
                        new KitSelector(player);
                    }
                    break;

                case ENDER_CHEST:
                    if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Kit Shop")) {
                        event.setCancelled(true);
                        new KitShop(player);
                    }
                    break;

                case BED:
                    if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Return to Hub")
                            && !config.getString("hub-server").equals("")) {
                        event.setCancelled(true);
                        MiscUtils.sendPlayer(player, config.getString("hub-server"));
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        KitUser user = KitUser.getInstance(player);

        if (!RegionUtil.isInRegion(player, player.getLocation()) && player.getWorld().getName().equals("world")
                && player.getGameMode() != GameMode.CREATIVE) {
            DeathListener.handleDeath(player);
            return;
        }

        if (!RegionUtil.isInSafezone(player, event.getTo()) && !user.hasKit() && !player.isDead()
                && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            spawn.teleport(player);
            MiscUtils.messagePlayer(player, "&cYou can't leave spawn without selecting a kit.");
            return;
        }

        if (RegionUtil.isInSafezone(player, event.getTo())) {
            if (combatLog.isInCombat(player)) {
                event.setTo(event.getFrom());
                MiscUtils.messagePlayer(player, "&cYou can't enter spawn while combat tagged.");
            } else {
                player.setHealth(20);
                player.setFireTicks(0);
            }
        }

        if (player.getWorld().getName().equals("world_the_end") && !combatLog.isInCombat(player)
                && player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            spawn.teleport(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortalEvent(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        KitUser user = KitUser.getInstance(player);

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            event.setCancelled(true);

            if (!user.hasKit()) {
                spawn.teleport(player);
                MiscUtils.messagePlayer(player, "&cYou can't enter The End without selecting a kit.");
                return;
            }

            endSpawn.teleport(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && player.hasMetadata("noFall")) {
                event.setCancelled(true);
                player.removeMetadata("noFall", kitPvP);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVoidDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID && player.getGameMode() != GameMode.CREATIVE) {
                DeathListener.handleDeath(player);
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
            event.getWorld().setStorm(false);
            event.getWorld().setThundering(false);
        }
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        if (event.toThunderState()) {
            event.setCancelled(true);
            event.getWorld().setStorm(false);
            event.getWorld().setThundering(false);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!(event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL
                && event.getPlayer().getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }
}
