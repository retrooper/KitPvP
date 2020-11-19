package net.foulest.kitpvp.listeners;

import net.foulest.kitpvp.KitPvP;
import net.foulest.kitpvp.utils.KitUser;
import net.foulest.kitpvp.utils.MiscUtils;
import net.foulest.kitpvp.utils.RegionUtil;
import net.foulest.kitpvp.utils.kits.Kit;
import net.foulest.kitpvp.utils.kits.KitManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class KitListener implements Listener {

    public static final Map<Player, Collection<PotionEffect>> DRAINED_EFFECTS = new HashMap<>();
    private final HashMap<UUID, Location> imprisonedPlayers;
    private final KitPvP kitPvP = KitPvP.getInstance();
    private final KitManager kitManager = KitManager.getInstance();

    public KitListener() {
        imprisonedPlayers = new HashMap<>();
    }

    @EventHandler
    public void onBurrowerAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);
        List<Location> roomLocations = getRoomLocations(player.getLocation());

        if (kitManager.hasRequiredKit(player, "Burrower") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.BRICK
                && !kitUser.hasCooldown(player, "Burrower")) {

            for (Location loc : roomLocations) {
                if (loc.getBlock().getType() != Material.AIR) {
                    MiscUtils.messagePlayer(player, "&cThere's not enough space above you to burrow.");
                    kitUser.setCooldown("Burrower", 5, true);
                    return;
                }
            }

            ArrayList<BlockState> pendingRollback = new ArrayList<>();

            for (Location location : roomLocations) {
                pendingRollback.add(location.getBlock().getState());
                location.getBlock().setType(Material.BRICK);
            }

            roomLocations.get(0).getBlock().setType(Material.GLOWSTONE);
            player.teleport(player.getLocation().add(0.0, 10.0, 0.0));

            Bukkit.getScheduler().scheduleSyncDelayedTask(kitPvP, () -> {
                for (BlockState block : pendingRollback) {
                    rollback(block);
                }
            }, 140);

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Burrower", 30, true);
            player.setMetadata("noFall", new FixedMetadataValue(kitPvP, true));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCactusHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player receiver = (Player) event.getEntity();
            KitUser damagerKitUser = KitUser.getInstance(damager);
            KitUser receiverKitUser = KitUser.getInstance(receiver);

            if (damagerKitUser.getKit().getName().equals("Cactus") && receiverKitUser.hasKit()) {
                receiver.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 0, false, false));
            }
        }
    }

    @EventHandler
    public void onDragonAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);

        if (kitManager.hasRequiredKit(player, "Dragon") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.FIREBALL
                && !kitUser.hasCooldown(player, "Dragon")) {

            player.playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
            player.playSound(player.getEyeLocation(), Sound.GHAST_FIREBALL, 1.0f, 0.0f);

            for (Entity entity : player.getNearbyEntities(5, 3, 5)) {
                if (entity instanceof Player) {
                    Player entityPlayer = (Player) entity;

                    if (!RegionUtil.isInSafezone(entityPlayer, entityPlayer.getLocation()) && player.hasLineOfSight(entity)) {
                        entityPlayer.damage(4, player);
                        entityPlayer.setFireTicks(150);
                    }
                }
            }

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Dragon", 30, true);
        }
    }

    @EventHandler
    public void onFishermanAbility(PlayerFishEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);

        if (kitManager.hasRequiredKit(player, "Fisherman") && !kitUser.hasCooldown(player, "Fisherman")
                && event.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY) && event.getCaught() instanceof Player) {

            Player caughtPlayer = (Player) event.getCaught();

            if (!RegionUtil.isInSafezone(caughtPlayer, caughtPlayer.getLocation())) {
                MiscUtils.messagePlayer(player, "&aYour ability has been used.");
                kitUser.setCooldown("Fisherman", 30, true);
                imprisonedPlayers.remove(caughtPlayer.getUniqueId());
                event.getCaught().teleport(player.getLocation());
            } else {
                kitUser.setCooldown("Fisherman", 5, true);
            }
        }
    }

    @EventHandler
    public void onGhostMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        double differenceXZ = Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());

        if (kitManager.hasRequiredKit(player, "Ghost") && !player.isSneaking()
                && differenceXZ != 0.0 && !RegionUtil.isInSafezone(player, player.getLocation())) {

            for (Entity entity : player.getNearbyEntities(6, 3, 6)) {
                if (entity instanceof Player) {
                    ((Player) entity).playSound(player.getLocation(), Sound.CHICKEN_WALK, 0.01f, 0.5f);
                }
            }
        }
    }

    @EventHandler
    public void onTamerAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);

        if (kitManager.hasRequiredKit(player, "Tamer") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.BONE
                && !kitUser.hasCooldown(player, "Tamer")) {

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Tamer", 30, true);
            ArrayList<Wolf> list = new ArrayList<>();

            for (int i = 0; i < 3; ++i) {
                Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
                wolf.setOwner(player);
                wolf.isAngry();
                wolf.setMaxHealth(20.0);
                wolf.setHealth(20.0);
                wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 2, false, false));
                list.add(wolf);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(kitPvP, () -> {
                for (Wolf wolf : list) {
                    wolf.remove();
                }
            }, 200);
        }
    }

    @EventHandler
    public void onHulkAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);
        List<Player> playerList = new ArrayList<>();

        if (kitManager.hasRequiredKit(player, "Hulk") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.PISTON_STICKY_BASE
                && !kitUser.hasCooldown(player, "Hulk")) {

            for (Entity entity : player.getNearbyEntities(6, 6, 6)) {
                if (entity instanceof Player) {
                    playerList.add((Player) entity);
                }
            }

            if (playerList.isEmpty()) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                MiscUtils.messagePlayer(player, "&cAbility failed; no players found nearby.");
                kitUser.setCooldown("Hulk", 5, true);
                return;
            }

            player.getWorld().createExplosion(player.getLocation(), 0.0f, false);

            for (Player playerInList : playerList) {
                if (!RegionUtil.isInSafezone(playerInList, playerInList.getLocation())) {
                    playerInList.getWorld().createExplosion(playerInList.getLocation(), 0.0f, false);
                    playerInList.damage(10, player);

                    Vector direction = playerInList.getEyeLocation().getDirection();
                    direction.multiply(-4);
                    direction.setY(1.0);
                    playerInList.setVelocity(direction);
                }
            }

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Hulk", 30, true);
        }
    }

    @EventHandler
    public void onImprisonerAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);

        if (kitManager.hasRequiredKit(player, "Imprisoner") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.DISPENSER
                && !kitUser.hasCooldown(player, "Imprisoner")) {

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Imprisoner", 30, true);
            player.launchProjectile(Snowball.class).setMetadata("prison", new FixedMetadataValue(kitPvP, true));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onImprisonerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball && ((Snowball) event.getDamager()).getShooter() instanceof Player) {
            Snowball snowball = (Snowball) event.getDamager();
            Player player = (Player) snowball.getShooter();
            KitUser kitUser = KitUser.getInstance(player);
            Entity entity = event.getEntity();

            if (kitManager.hasRequiredKit(player, "Imprisoner") && entity instanceof Player) {
                Player entityPlayer = (Player) entity;

                if (!RegionUtil.isInSafezone(entityPlayer, entityPlayer.getLocation())
                        && snowball.hasMetadata("prison") && !imprisonedPlayers.containsKey(entity.getUniqueId())) {

                    List<Block> cageBlocks = getCageBlocks(entity.getLocation().add(0.0, 9.0, 0.0));
                    for (Block cageBlock : cageBlocks) {
                        if (cageBlock.getType() != Material.AIR) {
                            MiscUtils.messagePlayer(player, "&cThere's not enough space above the target.");
                            kitUser.setCooldown("Imprisoner", 5, true);
                            return;
                        }
                    }

                    ArrayList<BlockState> pendingRollback = new ArrayList<>();

                    for (Block block : cageBlocks) {
                        pendingRollback.add(block.getState());
                    }

                    cageBlocks.get(0).setType(Material.MOSSY_COBBLESTONE);
                    for (int i = 1; i < 9; ++i) {
                        cageBlocks.get(i).setType(Material.IRON_FENCE);
                    }
                    cageBlocks.get(9).setType(Material.MOSSY_COBBLESTONE);
                    cageBlocks.get(10).setType(Material.LAVA);

                    entityPlayer.damage(4.0, player);

                    Location shouldBeLoc = entity.getLocation().add(0.0, 9.0, 0.0);
                    shouldBeLoc.setX(shouldBeLoc.getBlockX() + 0.5);
                    shouldBeLoc.setY(Math.floor(shouldBeLoc.getY()));
                    shouldBeLoc.setZ(shouldBeLoc.getBlockZ() + 0.5);
                    entity.teleport(shouldBeLoc);

                    imprisonedPlayers.put(entity.getUniqueId(), shouldBeLoc);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(kitPvP, () -> {
                        imprisonedPlayers.remove(entity.getUniqueId());
                        for (BlockState block : pendingRollback) {
                            rollback(block);
                        }
                    }, 60L);
                }
            }
        }
    }

    @EventHandler
    public void onKangarooAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Entity entityPlayer = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);

        if (kitManager.hasRequiredKit(player, "Kangaroo") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.FIREWORK
                && !kitUser.hasCooldown(player, "Kangaroo") && entityPlayer.isOnGround()) {

            Vector direction = player.getEyeLocation().getDirection();

            if (player.isSneaking()) {
                direction.setY(0.3);
                direction.multiply(3);
            } else {
                direction.setY(1.2);
            }

            player.setVelocity(direction);
            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Kangaroo", 20, true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMageAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);

        if (kitManager.hasRequiredKit(player, "Mage") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.GLOWSTONE_DUST
                && !kitUser.hasCooldown(player, "Mage")) {

            if (DRAINED_EFFECTS.containsKey(player)) {
                MiscUtils.messagePlayer(player, "&cAbility failed; your effects are still drained.");
                return;
            }

            int effect = MiscUtils.random.nextInt(21);
            int amplifier = MiscUtils.random.nextInt(3);
            int duration = Math.max(5, (MiscUtils.random.nextInt(30) + 1)) * 20;

            if (PotionEffectType.getById(effect).getName().equals("HUNGER")
                    || PotionEffectType.getById(effect).getName().equals("SATURATION")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier, false, false));
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.getById(effect), duration, amplifier, false, false));
            }

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Mage", 30, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMonkAbility(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);

        if (kitManager.hasRequiredKit(player, "Monk") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getRightClicked() instanceof Player && player.getItemInHand().getType() == Material.BLAZE_ROD
                && !kitUser.hasCooldown(player, "Monk")) {

            Player receiver = (Player) event.getRightClicked();
            KitUser receiverKitUser = KitUser.getInstance(receiver);

            if (receiverKitUser.hasKit() && !RegionUtil.isInSafezone(receiver, receiver.getLocation())) {
                int random = MiscUtils.random.nextInt(9);
                int heldItemSlot = receiver.getInventory().getHeldItemSlot();
                ItemStack itemInHand = receiver.getItemInHand();

                receiver.getInventory().setItem(heldItemSlot, receiver.getInventory().getItem(random));
                receiver.getInventory().setItem(random, itemInHand);
                receiver.updateInventory();

                MiscUtils.messagePlayer(player, "&aYour ability has been used.");
                kitUser.setCooldown("Monk", 30, true);
            }
        }
    }

    @EventHandler
    public void onSpidermanAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);

        if (kitManager.hasRequiredKit(player, "Spiderman") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.WEB
                && !kitUser.hasCooldown(player, "Spiderman")) {

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Spiderman", 15, true);
            player.launchProjectile(Snowball.class).setMetadata("spiderman", new FixedMetadataValue(kitPvP, true));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpidermanHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball && ((Snowball) event.getDamager()).getShooter() instanceof Player) {
            Snowball snowball = (Snowball) event.getDamager();
            Player player = (Player) snowball.getShooter();
            KitUser kitUser = KitUser.getInstance(player);
            Entity entity = event.getEntity();
            Location entityLocation = entity.getLocation();

            if (kitManager.hasRequiredKit(player, "Spiderman") && entity instanceof Player) {
                Player entityPlayer = (Player) entity;
                KitUser entityKitUser = KitUser.getInstance(entityPlayer);

                if (!entityKitUser.hasKit() || RegionUtil.isInSafezone(entityPlayer, entityPlayer.getLocation())) {
                    MiscUtils.messagePlayer(player, "&cYou can't use your ability on players in spawn.");
                    kitUser.setCooldown("Spiderman", 15, true);
                    return;
                }

                if (snowball.hasMetadata("spiderman")) {
                    ArrayList<BlockState> blockStates = new ArrayList<>();
                    Block block = entityLocation.getBlock();

                    while (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.STATIONARY_LAVA) {
                        entityLocation.add(0.0, 1.0, 0.0);
                        block = entityLocation.getBlock();
                    }

                    for (Location loc : getSurroundingLocations(entityLocation)) {
                        if (loc.getBlock().getType() == Material.WEB) {
                            continue;
                        }

                        for (Location loc2 : getSurroundingLocations(loc)) {
                            if (loc2.getBlock().getType() != Material.LADDER) {
                                blockStates.add(loc.getBlock().getState());
                            }
                        }
                    }

                    if (block.getType() != Material.WEB) {
                        blockStates.add(block.getState());
                    }

                    for (BlockState blockState : blockStates) {
                        blockState.getBlock().setType(Material.WEB);
                    }

                    Bukkit.getScheduler().scheduleSyncDelayedTask(kitPvP, () -> {
                        for (BlockState blockState : blockStates) {
                            rollback(blockState);
                        }
                    }, 140);

                    MiscUtils.messagePlayer(player, "&aYour ability has been used.");
                }
            }
        }
    }

    @EventHandler
    public void onSummonerAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);
        List<Player> playerList = new ArrayList<>();

        if (kitManager.hasRequiredKit(player, "Summoner") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.IRON_BLOCK
                && !kitUser.hasCooldown(player, "Summoner")) {

            for (Entity entity : player.getNearbyEntities(15, 5, 15)) {
                if (entity instanceof Player) {
                    playerList.add((Player) entity);
                }
            }

            if (playerList.isEmpty()) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                MiscUtils.messagePlayer(player, "&cAbility failed; no players found nearby.");
                kitUser.setCooldown("Summoner", 5, true);
                return;
            }

            IronGolem ironGolem = (IronGolem) player.getWorld().spawnEntity(player.getLocation(), EntityType.IRON_GOLEM);

            ironGolem.setMetadata(player.getName(), new FixedMetadataValue(kitPvP, true));
            ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 99999, 1, false, false));
            ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 50, false, false));

            for (Player playerInList : playerList) {
                if (!RegionUtil.isInSafezone(playerInList, playerInList.getLocation())) {
                    ironGolem.setTarget(playerInList);
                }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(kitPvP, ironGolem::remove, 200);

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Summoner", 30, true);
        }
    }

    @EventHandler
    public void onThorAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);
        List<Player> playerList = new ArrayList<>();

        if (kitManager.hasRequiredKit(player, "Thor") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.IRON_AXE
                && !kitUser.hasCooldown(player, "Thor")) {

            for (Entity entity : player.getNearbyEntities(6, 6, 6)) {
                if (entity instanceof Player) {
                    playerList.add((Player) entity);
                }
            }

            if (playerList.isEmpty()) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                MiscUtils.messagePlayer(player, "&cAbility failed; no players found nearby.");
                kitUser.setCooldown("Thor", 5, true);
                return;
            }

            for (Player playerInList : playerList) {
                if (!RegionUtil.isInSafezone(playerInList, playerInList.getLocation())) {
                    player.getWorld().strikeLightningEffect(playerInList.getLocation());
                    playerInList.damage(12, player);
                    playerInList.setFireTicks(100);
                }
            }

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Thor", 30, true);
        }
    }

    @EventHandler
    public void onTimelordAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);
        List<Player> playerList = new ArrayList<>();

        if (kitManager.hasRequiredKit(player, "Timelord") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.WATCH
                && !kitUser.hasCooldown(player, "Timelord")) {

            for (Entity entity : player.getNearbyEntities(6, 6, 6)) {
                if (entity instanceof Player) {
                    playerList.add((Player) entity);
                }
            }

            if (playerList.isEmpty()) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                MiscUtils.messagePlayer(player, "&cAbility failed; no players found nearby.");
                kitUser.setCooldown("Timelord", 5, true);
                return;
            }

            for (Player playerInList : playerList) {
                if (!RegionUtil.isInSafezone(playerInList, playerInList.getLocation())) {
                    playerInList.getWorld().playEffect(playerInList.getLocation(), Effect.STEP_SOUND, 152);
                    playerInList.getWorld().playEffect(playerInList.getLocation().add(0.0, 1.0, 0.0), Effect.STEP_SOUND, 152);
                    playerInList.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 10, false, false));
                    playerInList.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 254, false, false));
                    playerInList.playSound(playerInList.getLocation(), Sound.GHAST_FIREBALL, 1, 1);
                }
            }

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Timelord", 30, true);
            player.playSound(player.getLocation(), Sound.WITHER_SHOOT, 1, 1);
        }
    }

    @EventHandler
    public void onVampireAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);
        List<Player> playerList = new ArrayList<>();
        List<PotionEffect> playerEffects = new ArrayList<>();

        if (kitManager.hasRequiredKit(player, "Vampire") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.REDSTONE
                && !kitUser.hasCooldown(player, "Vampire")) {

            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Player && !RegionUtil.isInSafezone((Player) entity, entity.getLocation())) {
                    playerList.add((Player) entity);
                }
            }

            if (playerList.isEmpty()) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                MiscUtils.messagePlayer(player, "&cAbility failed; no players found nearby.");
                kitUser.setCooldown("Vampire", 5, true);
                return;
            }

            for (Player playerInList : playerList) {
                playerEffects.addAll(playerInList.getActivePotionEffects());
            }

            if (playerEffects.isEmpty()) {
                MiscUtils.messagePlayer(player, "&cAbility failed; no effects found to drain.");
                kitUser.setCooldown("Vampire", 5, true);
                return;
            }

            for (Player playerInList : playerList) {
                DRAINED_EFFECTS.put(playerInList, playerInList.getActivePotionEffects());
                MiscUtils.messagePlayer(playerInList, "&cYour effects have been drained by a Vampire!");

                for (PotionEffect potionEffect : DRAINED_EFFECTS.get(playerInList)) {
                    if (potionEffect.getType() != PotionEffectType.INCREASE_DAMAGE) {
                        playerInList.playSound(playerInList.getLocation(), Sound.CAT_HISS, 1, 1);
                        playerInList.removePotionEffect(potionEffect.getType());
                        player.addPotionEffect(potionEffect);
                        MiscUtils.messagePlayer(player, ("&aYou drained the " + potionEffect.getType().getName() + " effect from " + playerInList.getName() + "."));
                    }
                }

                KitUser kitUserInList = KitUser.getInstance(playerInList);
                Kit currentKit = kitUserInList.getKit();

                new BukkitRunnable() {
                    public void run() {
                        if (!player.getActivePotionEffects().isEmpty()) {
                            for (PotionEffect effect : player.getActivePotionEffects()) {
                                player.removePotionEffect(effect.getType());
                            }
                            MiscUtils.messagePlayer(player, "&cYour drained effects were removed.");
                        }

                        if (!DRAINED_EFFECTS.get(playerInList).isEmpty() && kitUserInList.hasKit() && currentKit == kitUserInList.getKit()) {
                            for (PotionEffect effect : DRAINED_EFFECTS.get(playerInList)) {
                                playerInList.addPotionEffect(effect);
                            }
                            MiscUtils.messagePlayer(playerInList, "&aYour drained effects were restored.");
                        }
                    }
                }.runTaskLater(kitPvP, 200);
            }

            MiscUtils.messagePlayer(player, "&aYour ability has been used.");
            kitUser.setCooldown("Vampire", 30, true);
            player.playSound(player.getLocation(), Sound.CAT_HISS, 1, 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVampireHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player receiver = (Player) event.getEntity();
            KitUser damagerKitUser = KitUser.getInstance(damager);
            KitUser receiverKitUser = KitUser.getInstance(receiver);

            if (damagerKitUser.getKit().getName().equals("Vampire") && receiverKitUser.hasKit()) {
                damager.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 0, false, false));
            }
        }
    }

    @EventHandler
    public void onZenAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        KitUser kitUser = KitUser.getInstance(player);
        List<Player> playerList = new ArrayList<>();

        if (kitManager.hasRequiredKit(player, "Zen") && !RegionUtil.isInSafezone(player, player.getLocation())
                && event.getAction().toString().contains("RIGHT") && player.getItemInHand().getType() == Material.SLIME_BALL
                && !kitUser.hasCooldown(player, "Zen")) {

            Player closest = null;
            double closestDistance = 0;

            for (Entity entity : player.getNearbyEntities(25, 25, 25)) {
                if (entity instanceof Player) {
                    playerList.add((Player) entity);
                }
            }

            if (playerList.isEmpty()) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                MiscUtils.messagePlayer(player, "&cAbility failed; no players found nearby.");
                kitUser.setCooldown("Zen", 5, true);
                return;
            }

            for (Player playerInList : playerList) {
                if (!RegionUtil.isInSafezone(playerInList, playerInList.getLocation())) {
                    double distance = playerInList.getLocation().distanceSquared(player.getLocation());

                    if (closest == null || distance < closestDistance) {
                        closest = playerInList;
                        closestDistance = distance;
                    }
                }
            }

            if (closest != null) {
                imprisonedPlayers.remove(player.getUniqueId());
                player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 1);
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                player.teleport(closest.getLocation());
                player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 1);
                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                closest.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false));
                MiscUtils.messagePlayer(player, "&aYou teleported to " + closest.getDisplayName() + ".");
                kitUser.setCooldown("Zen", 30, true);
            }
        }
    }

    public List<Block> getCageBlocks(Location location) {
        ArrayList<Block> list = new ArrayList<>();

        list.add(location.clone().add(0.0, -1.0, 0.0).getBlock());
        list.add(location.clone().add(-1.0, 0.0, 0.0).getBlock());
        list.add(location.clone().add(0.0, 0.0, 1.0).getBlock());
        list.add(location.clone().add(0.0, 0.0, -1.0).getBlock());
        list.add(location.clone().add(1.0, 0.0, 0.0).getBlock());
        list.add(location.clone().add(-1.0, 0.0, -1.0).getBlock());
        list.add(location.clone().add(-1.0, 0.0, 1.0).getBlock());
        list.add(location.clone().add(1.0, 0.0, -1.0).getBlock());
        list.add(location.clone().add(1.0, 0.0, 1.0).getBlock());
        list.add(location.clone().add(0.0, 2.0, 0.0).getBlock());
        list.add(location.getBlock());
        list.add(location.add(0.0, 1.0, 0.0).getBlock());
        return list;
    }

    public List<Location> getPlatform(Location location) {
        ArrayList<Location> list = new ArrayList<>();

        list.add(location.clone());
        list.add(location.clone().add(-1.0, 0.0, 0.0));
        list.add(location.clone().add(0.0, 0.0, -1.0));
        list.add(location.clone().add(1.0, 0.0, 0.0));
        list.add(location.clone().add(0.0, 0.0, 1.0));
        list.add(location.clone().add(-1.0, 0.0, -1.0));
        list.add(location.clone().add(1.0, 0.0, -1.0));
        list.add(location.clone().add(1.0, 0.0, 1.0));
        list.add(location.clone().add(-1.0, 0.0, 1.0));
        return list;
    }

    public List<Location> getRoomLocations(Location location) {
        location.add(0.0, 9.0, 0.0);

        ArrayList<Location> list = new ArrayList<>(this.getPlatform(location));

        for (int i = 0; i < 3; ++i) {
            location.add(0.0, 1.0, 0.0);
            list.add(location.clone().add(0.0, 0.0, -2.0));
            list.add(location.clone().add(0.0, 0.0, 2.0));
            list.add(location.clone().add(2.0, 0.0, 0.0));
            list.add(location.clone().add(-2.0, 0.0, 0.0));
            list.add(location.clone().add(-2.0, 0.0, 2.0));
            list.add(location.clone().add(-2.0, 0.0, -2.0));
            list.add(location.clone().add(2.0, 0.0, -2.0));
            list.add(location.clone().add(2.0, 0.0, 2.0));
            list.add(location.clone().add(1.0, 0.0, 2.0));
            list.add(location.clone().add(-1.0, 0.0, 2.0));
            list.add(location.clone().add(-2.0, 0.0, 1.0));
            list.add(location.clone().add(-2.0, 0.0, -1.0));
            list.add(location.clone().add(-1.0, 0.0, -2.0));
            list.add(location.clone().add(1.0, 0.0, -2.0));
            list.add(location.clone().add(2.0, 0.0, -1.0));
            list.add(location.clone().add(2.0, 0.0, 1.0));
        }

        list.addAll(this.getPlatform(location.add(0.0, 1.0, 0.0)));
        return list;
    }

    public List<Location> getSurroundingLocations(Location location) {
        ArrayList<Location> list = new ArrayList<>();

        list.add(location.clone().add(-1.0, 0.0, 0.0));
        list.add(location.clone().add(0.0, 0.0, 1.0));
        list.add(location.clone().add(0.0, 0.0, -1.0));
        list.add(location.clone().add(1.0, 0.0, 0.0));
        return list;
    }

    public void rollback(BlockState blockState) {
        if (blockState instanceof Sign) {
            Sign sign = (Sign) blockState;
            Location location = sign.getLocation();

            location.getWorld().getBlockAt(location).setType(blockState.getType());

            Sign sign2 = (Sign) location.getWorld().getBlockAt(location).getState();

            for (int i = 0; i < 4; ++i) {
                sign2.setLine(i, sign.getLines()[i]);
            }

            sign2.update(true);
        } else {
            blockState.update(true);
        }
    }
}
