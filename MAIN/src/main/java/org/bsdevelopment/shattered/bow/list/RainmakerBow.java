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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@BowData(
        name = "Rainmaker",
        description = {"Rains arrows down on your enemies"},
        type = {BowType.OFFENSIVE}
)
public class RainmakerBow extends ShatteredBow implements AirTask {
    private final Map<UUID, Integer> FIRE_MAP;

    public RainmakerBow() {
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
            for (int i = 0; i < 4; i++) {
                float x = MathUtils.random(-1, 1);
                float z = MathUtils.random(-1, 1);

                ShatteredUtilities.spawnArrowRandomized(info, currentLocation, new Vector(x, -1, z));
            }
            fires = 0;
        }

        FIRE_MAP.put(arrow.getUniqueId(), fires);
    }
}
