package org.bsdevelopment.shattered.events.gamemode;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

/**
 * This event is called when a game mode is about to start
 */
public class ShatteredGamemodePreStartEvent extends ShatteredEvent {
    private final ShatteredGameMode GAMEMODE;

    public ShatteredGamemodePreStartEvent(ShatteredGameMode gameMode) {
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
