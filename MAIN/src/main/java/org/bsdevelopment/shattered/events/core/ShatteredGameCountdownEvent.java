package org.bsdevelopment.shattered.events.core;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

/**
 * It's an event that is called every second during the game countdown
 */
public class ShatteredGameCountdownEvent extends ShatteredEvent {
    private final int CURRENT_TIME;
    private final ShatteredGameMode GAMEMODE;

    public ShatteredGameCountdownEvent(int current_time, ShatteredGameMode gamemode) {
        CURRENT_TIME = current_time;
        GAMEMODE = gamemode;
    }

    /**
     * This function returns the current time.
     *
     * @return The current time.
     */
    public int getCurrentTime() {
        return CURRENT_TIME;
    }

    /**
     * It returns the gamemode of the game
     *
     * @return The GAMEMODE variable.
     */
    public ShatteredGameMode getGamemode() {
        return GAMEMODE;
    }
}
