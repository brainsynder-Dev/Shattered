package org.bsdevelopment.shattered.events.core;

import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.events.ShatteredEvent;

/**
 * It's an event that is fired when a map is cleared
 */
public class ShatteredMapClearEvent extends ShatteredEvent {
    private final Cuboid REGION;

    public ShatteredMapClearEvent(Cuboid region) {
        REGION = region;
    }

    /**
     * Returns the region of the world that this map was in.
     *
     * @return The region of the map.
     */
    public Cuboid getRegion() {
        return REGION;
    }
}
