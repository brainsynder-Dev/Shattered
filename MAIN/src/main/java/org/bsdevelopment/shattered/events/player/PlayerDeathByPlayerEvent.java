package org.bsdevelopment.shattered.events.player;

import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

public class PlayerDeathByPlayerEvent extends PlayerDeathEvent {
    private final ShatteredPlayer KILLER;

    public PlayerDeathByPlayerEvent(ShatteredPlayer shattered_player, ShatteredGameMode gameMode, ShatteredGameMode.DeathReasons reason, ShatteredPlayer killer) {
        super(shattered_player, gameMode, reason);
        KILLER = killer;
    }

    public ShatteredPlayer getPlayerKiller() {
        return KILLER;
    }
}
