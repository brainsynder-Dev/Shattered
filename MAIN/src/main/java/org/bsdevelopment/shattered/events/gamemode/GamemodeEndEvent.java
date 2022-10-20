package org.bsdevelopment.shattered.events.gamemode;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

public class GamemodeEndEvent extends ShatteredEvent {
    private final ShatteredGameMode GAMEMODE;

    public GamemodeEndEvent(ShatteredGameMode gameMode) {
        this.GAMEMODE = gameMode;
    }

    public ShatteredGameMode getGamemode() {
        return GAMEMODE;
    }
}
