package org.bsdevelopment.shattered.events.lobby;

import org.bsdevelopment.shattered.events.ShatteredCancelEvent;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.option.Option;

/**
 * This event is called when a player changes a game option
 */
public class ShatteredGameOptionChangeEvent extends ShatteredCancelEvent {
    private final ShatteredPlayer SHATTERED_PLAYER;
    private final Option<?> OPTION;

    public ShatteredGameOptionChangeEvent(ShatteredPlayer shattered_player, Option<?> option) {
        SHATTERED_PLAYER = shattered_player;
        OPTION = option;
    }

    /**
     * The option that is being changed
     *
     * @return The Option object.
     */
    public Option<?> getOption() {
        return OPTION;
    }

    /**
     * It returns the ShatteredPlayer object that is associated with the player
     *
     * @return The ShatteredPlayer object.
     */
    public ShatteredPlayer getShatteredPlayer() {
        return SHATTERED_PLAYER;
    }
}
