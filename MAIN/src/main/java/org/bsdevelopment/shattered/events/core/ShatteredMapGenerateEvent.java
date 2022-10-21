package org.bsdevelopment.shattered.events.core;

import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.events.ShatteredEvent;

/**
 * `MapGenerateEvent` is an event that is fired when a map is generated
 */
public class ShatteredMapGenerateEvent extends ShatteredEvent {
    private final Cuboid REGION;

    public ShatteredMapGenerateEvent(Cuboid region) {
        REGION = region;
    }

    /**
     * Returns the region of the world that this map is in.
     *
     * @return The region of the map.
     */
    public Cuboid getRegion() {
        return REGION;
    }
}
