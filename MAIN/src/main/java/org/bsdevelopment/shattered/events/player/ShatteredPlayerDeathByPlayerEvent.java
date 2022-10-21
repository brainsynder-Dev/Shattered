package org.bsdevelopment.shattered.events.player;

import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

/**
 * It's a PlayerDeathEvent that has a killer
 */
public class ShatteredPlayerDeathByPlayerEvent extends ShatteredPlayerDeathEvent {
    private final ShatteredPlayer KILLER;

    public ShatteredPlayerDeathByPlayerEvent(ShatteredPlayer shattered_player, ShatteredGameMode gameMode, ShatteredGameMode.DeathReasons reason, ShatteredPlayer killer) {
        super(shattered_player, gameMode, reason);
        KILLER = killer;
    }

    /**
     * This function returns the player who killed the player who called this function.
     *
     * @return The player who killed the player.
     */
    public ShatteredPlayer getPlayerKiller() {
        return KILLER;
    }
}
