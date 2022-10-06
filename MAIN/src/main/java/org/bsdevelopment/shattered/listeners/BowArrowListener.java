package org.bsdevelopment.shattered.listeners;

import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.UnbreakableGlass;
import org.bsdevelopment.shattered.bow.data.BowForce;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.bow.tasks.*;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.BowInfoPersistentData;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class BowArrowListener implements Listener {
    private final Shattered PLUGIN;

    public BowArrowListener(Shattered plugin) {
        PLUGIN = plugin;
    }


    @EventHandler
    public void onBowShoot (EntityShootBowEvent event) {
        if (!(event.getProjectile() instanceof Arrow arrow)) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getBow() == null) return;

        ShatteredBow bow = Management.BOW_MANAGER.getBow(Objects.requireNonNull(event.getBow()));

        Cuboid region = PLUGIN.getSchematics().getCurrentRegion();
        if ((bow != null) &&
                ((region == null) || (!region.contains(ShatteredUtilities.getInfiniteY(region, player.getLocation()))))) {
            event.setCancelled(true);
            return;
        }


        BowForce force = BowForce.getForce(event.getForce());
        BowInfo info = new BowInfo(player, arrow.getLocation(), force).setBow(bow);

        arrow.getPersistentDataContainer().set(Management.KEY_MANAGER.BOW_INFO_KEY, BowInfoPersistentData.INSTANCE, info);

        if (bow instanceof LaunchTask launchTask) launchTask.onLaunch(arrow, info);
        if (bow instanceof AirTask airTask) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!ShatteredUtilities.isValid(arrow)) {
                        cancel();
                        if (bow instanceof RemoveTask removeTask) removeTask.onRemove(arrow, info);
                        return;
                    }

                    if ((PLUGIN.getSchematics().getCurrentRegion() == null)
                            || (!region.contains(ShatteredUtilities.getInfiniteY(region, arrow.getLocation())))) {
                        cancel();
                        arrow.remove();
                        return;
                    }

                    if (arrow.isInBlock()) {
                        cancel();
                        return;
                    }

                    airTask.whileInAir(arrow, info, arrow.getLocation());
                }
            }.runTaskTimer(PLUGIN, 0, 1);
        }
    }


    @EventHandler
    public void onArrowHit (ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        PersistentDataContainer container = arrow.getPersistentDataContainer();

        if (!container.has(Management.KEY_MANAGER.BOW_INFO_KEY, BowInfoPersistentData.INSTANCE)) return;

        Cuboid region = PLUGIN.getSchematics().getCurrentRegion();
        if (region == null) {
            arrow.remove();
            return;
        }

        BowInfo info = arrow.getPersistentDataContainer().get(Management.KEY_MANAGER.BOW_INFO_KEY, BowInfoPersistentData.INSTANCE);
        ShatteredBow bow = Objects.requireNonNull(info).getBow();

        Location location = arrow.getLocation();
        if (event.getHitBlock() != null) location = event.getHitBlock().getLocation();
        if (event.getHitEntity() != null) location = event.getHitEntity().getLocation();
        info.setEndLocation(location);

        if (event.getHitBlockFace() != null) info.setBlockFace(event.getHitBlockFace());

        if (bow != null) {
            if (!region.contains(ShatteredUtilities.getInfiniteY(region, arrow.getLocation()))) return;


            if ((event.getHitEntity() != null) && (event.getHitEntity() instanceof Player player)) {
                ShatteredGameMode gameMode = Management.GAME_MANAGER.getCurrentGamemode();
                if ((gameMode != null) && (gameMode.canDamagePlayer(
                        Management.PLAYER_MANAGER.getShatteredPlayer(player),
                        Management.PLAYER_MANAGER.getShatteredPlayer(Objects.requireNonNull(info.getShooter()))
                ))) {
                    if (bow instanceof HitPlayerTask hitPlayerTask) hitPlayerTask.onHit(arrow, info, player);
                }
            } else if (bow instanceof HitTask hitTask) hitTask.onHit(arrow, info);
            if (bow instanceof RemoveTask removeTask) removeTask.onRemove(arrow, info);

            if (bow.fetchBowData().removeArrowOnHit()) arrow.remove();

            if (bow.getClass().isAnnotationPresent(UnbreakableGlass.class)) return;
        }

        Block block = event.getHitBlock();
        if (block == null) return;

        // TODO: Maybe add a BlockDegradation class to list how many times a block has degraded...
        ShatteredGameMode gameMode = Management.GAME_MANAGER.getCurrentGamemode();
        if (gameMode != null) gameMode.onArrowHitBlock(info, block);
        Management.GLASS_MANAGER.handleGlass(block);
    }




    @EventHandler
    public void onArrowChildHit (ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        PersistentDataContainer container = arrow.getPersistentDataContainer();

        if (!container.has(Management.KEY_MANAGER.ARROW_CHILD_KEY, BowInfoPersistentData.INSTANCE)) return;

        Cuboid region = PLUGIN.getSchematics().getCurrentRegion();
        if (region == null) {
            arrow.remove();
            return;
        }

        BowInfo info = arrow.getPersistentDataContainer().get(Management.KEY_MANAGER.ARROW_CHILD_KEY, BowInfoPersistentData.INSTANCE);
        ShatteredBow bow = Objects.requireNonNull(info).getBow();

        if (bow != null) {
            if (!region.contains(ShatteredUtilities.getInfiniteY(region, arrow.getLocation()))) {
                arrow.remove();
                return;
            }

            arrow.remove();

            if (bow.getClass().isAnnotationPresent(UnbreakableGlass.class)) return;
        }

        Block block = event.getHitBlock();
        if (block == null) return;

        // TODO: Add a method from the Gamemode class to handle when an arrow
        // Hits a block... eg: FruitBuster Gamemode, where the arrow hits a skull
        Management.GLASS_MANAGER.handleGlass(block);
    }
}
