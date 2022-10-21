package org.bsdevelopment.shattered.events.core;

import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.events.ShatteredEvent;

public class MapGenerateEvent extends ShatteredEvent {
    private final Cuboid REGION;

    public MapGenerateEvent(Cuboid region) {
        REGION = region;
    }

    public Cuboid getRegion() {
        return REGION;
    }
}
