package org.bsdevelopment.shattered.bow.list;

import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.BowData;
import org.bsdevelopment.shattered.bow.annotations.UnbreakableGlass;
import org.bsdevelopment.shattered.bow.data.BowForce;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.bow.data.BowType;
import org.bsdevelopment.shattered.bow.tasks.HitTask;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.entity.Arrow;

import java.util.Objects;

@BowData(
        name = "Fixer",
        description = {"Regenerates areas of the map"},
        type = {BowType.UTILITY, BowType.SUPPORT},
        spawnChance = 38
)
@UnbreakableGlass
public class FixerBow extends ShatteredBow implements HitTask {

    @Override
    public void cleanup() {}

    @Override
    public void onHit(Arrow arrow, BowInfo info) {
        int radius = 2;
        if (info.getForce() == BowForce.MEDIUM) radius = 3;
        if (info.getForce() == BowForce.MAX) radius = 4;


        ShatteredUtilities.getBlocksInRadius(Objects.requireNonNull(info.getEndLocation()), radius, false).forEach(block -> {
            if (Management.GLASS_MANAGER.isSaved(block)) {
                Management.GAME_STATS_MANAGER.PATCHED_BLOCKS.increase();
                Management.GLASS_MANAGER.reset(block.getLocation());

                block.getWorld().spawnParticle(org.bukkit.Particle.TOTEM, block.getLocation().add(0.5, 0, 0.5), 10, 0.75, 0.75, 0.75, 0);
            }
        });
    }
}
