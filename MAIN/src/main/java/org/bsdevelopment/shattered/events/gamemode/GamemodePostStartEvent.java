package org.bsdevelopment.shattered.events.gamemode;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

public class GamemodePostStartEvent extends ShatteredEvent {
    private final ShatteredGameMode GAMEMODE;

    public GamemodePostStartEvent(ShatteredGameMode gameMode) {
        this.GAMEMODE = gameMode;
    }

    public ShatteredGameMode getGamemode() {
        return GAMEMODE;
    }
}
