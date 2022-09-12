package org.bsdevelopment.shattered.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ShatteredEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    //

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
