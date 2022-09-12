package org.bsdevelopment.shattered.listeners;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.UnbreakableGlass;
import org.bsdevelopment.shattered.bow.data.BowForce;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.bow.tasks.*;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.BowInfoPersistentData;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Location;
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

        ShatteredBow bow = Management.BOW_MANAGER.getBow(Objects.requireNonNull(event.getBow()));

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
                        if (bow instanceof RemoveTask removeTask) removeTask.onRemove(info);
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
        BowInfo info = arrow.getPersistentDataContainer().get(Management.KEY_MANAGER.BOW_INFO_KEY, BowInfoPersistentData.INSTANCE);
        ShatteredBow bow = Objects.requireNonNull(info).getBow();

        Player shooter = info.getShooter();

        Location location = arrow.getLocation(), cloned = arrow.getLocation().clone();
        if (event.getHitBlock() != null) location = event.getHitBlock().getLocation();
        if (event.getHitEntity() != null) location = event.getHitEntity().getLocation();
        info.setEndLocation(location);

        if (event.getHitBlockFace() != null) info.setBlockFace(event.getHitBlockFace());

        if (bow != null) {

            if ((event.getHitEntity() != null) && (event.getHitEntity() instanceof Player player)) {
                // TODO: Need to do the checks for teams and such

                if (bow instanceof HitPlayerTask hitPlayerTask) hitPlayerTask.onHit(info, player);
            } else if (bow instanceof HitTask hitTask) hitTask.onHit(info);
            if (bow instanceof RemoveTask removeTask) removeTask.onRemove(info);

            if (bow.getClass().isAnnotationPresent(UnbreakableGlass.class)) return;
        }

        // TODO: Need to run the glass shattering code here...
    }
}
