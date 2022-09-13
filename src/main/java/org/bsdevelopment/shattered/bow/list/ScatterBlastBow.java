package org.bsdevelopment.shattered.bow.list;

import lib.brainsynder.math.MathUtils;
import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.BowData;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.bow.data.BowType;
import org.bsdevelopment.shattered.bow.tasks.AirTask;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@BowData(
        name = "ScatterBlast",
        description = {"Fires arrows in the same direction as the fired arrow"},
        type = {BowType.OFFENSIVE},

        spawnChance = 38
)
public class ScatterBlastBow extends ShatteredBow implements AirTask {
    private final Map<UUID, Integer> FIRE_MAP;

    public ScatterBlastBow() {
        FIRE_MAP = new HashMap<>();
    }

    @Override
    public void cleanup() {
        FIRE_MAP.clear();
    }

    @Override
    public void whileInAir(Arrow arrow, BowInfo info, Location currentLocation) {
        int fires = FIRE_MAP.getOrDefault(arrow.getUniqueId(), 0);
        if (fires < 2) fires++;

        if (fires >= 2) {
            for (int i = 0; i < 3; i++) {
                ShatteredUtilities.spawnArrowRandomized(info, currentLocation, ShatteredUtilities.spread(arrow.getVelocity(), MathUtils.random(-0.9F, 0.9F)));
            }
            fires = 0;
        }

        FIRE_MAP.put(arrow.getUniqueId(), fires);
    }
}
