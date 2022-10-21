package org.bsdevelopment.shattered.events.player;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

/**
 * This event is called when a player is eliminated from a game
 */
public class ShatteredPlayerEliminatedEvent extends ShatteredEvent {
    private final ShatteredPlayer SHATTERED_PLAYER;
    private final ShatteredGameMode GAMEMODE;

    public ShatteredPlayerEliminatedEvent(ShatteredPlayer shattered_player, ShatteredGameMode gameMode) {
        SHATTERED_PLAYER = shattered_player;
        this.GAMEMODE = gameMode;
    }

    /**
     * It returns the ShatteredPlayer object that is associated with the player
     *
     * @return The ShatteredPlayer object.
     */
    public ShatteredPlayer getShatteredPlayer() {
        return SHATTERED_PLAYER;
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
