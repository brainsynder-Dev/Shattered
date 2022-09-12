package org.bsdevelopment.shattered.events;

import org.bukkit.event.Cancellable;

public class ShatteredCancelEvent extends ShatteredEvent implements Cancellable {
    private boolean CANCEL = false;



    @Override
    public boolean isCancelled() {
        return CANCEL;
    }

    @Override
    public void setCancelled(boolean cancel) {
        CANCEL = cancel;
    }
}
