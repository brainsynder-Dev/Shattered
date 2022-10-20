package org.bsdevelopment.shattered.events.gamemode;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

public class GamemodePreStartEvent extends ShatteredEvent {
    private final ShatteredGameMode GAMEMODE;

    public GamemodePreStartEvent(ShatteredGameMode gameMode) {
        this.GAMEMODE = gameMode;
    }

    public ShatteredGameMode getGamemode() {
        return GAMEMODE;
    }
}
