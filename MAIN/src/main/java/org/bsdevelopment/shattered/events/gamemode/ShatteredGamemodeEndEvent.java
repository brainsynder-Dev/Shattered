package org.bsdevelopment.shattered.events.gamemode;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

/**
 * This event is called when a gamemode ends
 */
public class ShatteredGamemodeEndEvent extends ShatteredEvent {
    private final ShatteredGameMode GAMEMODE;

    public ShatteredGamemodeEndEvent(ShatteredGameMode gameMode) {
        this.GAMEMODE = gameMode;
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
