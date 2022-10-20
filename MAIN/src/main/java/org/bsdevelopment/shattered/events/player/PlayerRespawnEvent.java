package org.bsdevelopment.shattered.events.player;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

public class PlayerRespawnEvent extends ShatteredEvent {
    private final ShatteredPlayer SHATTERED_PLAYER;
    private final ShatteredGameMode GAMEMODE;

    public PlayerRespawnEvent(ShatteredPlayer shattered_player, ShatteredGameMode gameMode) {
        SHATTERED_PLAYER = shattered_player;
        this.GAMEMODE = gameMode;
    }

    public ShatteredPlayer getShatteredPlayer() {
        return SHATTERED_PLAYER;
    }

    public ShatteredGameMode getGamemode() {
        return GAMEMODE;
    }
}
