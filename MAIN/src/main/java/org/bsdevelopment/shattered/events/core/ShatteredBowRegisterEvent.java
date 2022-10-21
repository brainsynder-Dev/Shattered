package org.bsdevelopment.shattered.events.core;

import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.events.ShatteredEvent;

/**
 * It's an event that is called when a bow is registered
 */
public class ShatteredBowRegisterEvent extends ShatteredEvent {
    private final ShatteredBow BOW;

    public ShatteredBowRegisterEvent(ShatteredBow bow) {
        this.BOW = bow;
    }

    /**
     * This function returns the bow.
     *
     * @return The bow object.
     */
    public ShatteredBow getBow() {
        return BOW;
    }
}
