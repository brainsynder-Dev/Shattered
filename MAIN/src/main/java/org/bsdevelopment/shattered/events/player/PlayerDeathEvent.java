package org.bsdevelopment.shattered.events.player;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

public class PlayerDeathEvent extends ShatteredEvent {
    private final ShatteredPlayer SHATTERED_PLAYER;
    private final ShatteredGameMode GAMEMODE;
    private final ShatteredGameMode.DeathReasons REASON;

    public PlayerDeathEvent(ShatteredPlayer shattered_player, ShatteredGameMode gameMode, ShatteredGameMode.DeathReasons reason) {
        SHATTERED_PLAYER = shattered_player;
        this.GAMEMODE = gameMode;
        REASON = reason;
    }

    public ShatteredPlayer getShatteredPlayer() {
        return SHATTERED_PLAYER;
    }

    public ShatteredGameMode getGamemode() {
        return GAMEMODE;
    }

    public ShatteredGameMode.DeathReasons getReason() {
        return REASON;
    }
}
