package org.bsdevelopment.shattered.events.player;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

/**
 * It's a class that is called when a player dies
 */
public class ShatteredPlayerDeathEvent extends ShatteredEvent {
    private final ShatteredPlayer SHATTERED_PLAYER;
    private final ShatteredGameMode GAMEMODE;
    private final ShatteredGameMode.DeathReasons REASON;

    public ShatteredPlayerDeathEvent(ShatteredPlayer shattered_player, ShatteredGameMode gameMode, ShatteredGameMode.DeathReasons reason) {
        SHATTERED_PLAYER = shattered_player;
        this.GAMEMODE = gameMode;
        REASON = reason;
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

    /**
     * It returns the reason the player died
     *
     * @return The reason for the player's death.
     */
    public ShatteredGameMode.DeathReasons getReason() {
        return REASON;
    }
}
