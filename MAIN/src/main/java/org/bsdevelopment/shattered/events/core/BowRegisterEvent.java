package org.bsdevelopment.shattered.events.core;

import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.events.ShatteredEvent;

public class BowRegisterEvent extends ShatteredEvent {
    private final ShatteredBow BOW;

    public BowRegisterEvent(ShatteredBow bow) {
        this.BOW = bow;
    }

    public ShatteredBow getBow() {
        return BOW;
    }
}
