package org.bsdevelopment.shattered.events.core;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

public class GameCountdownEvent extends ShatteredEvent {
    private final int CURRENT_TIME;
    private final ShatteredGameMode GAMEMODE;

    public GameCountdownEvent(int current_time, ShatteredGameMode gamemode) {
        CURRENT_TIME = current_time;
        GAMEMODE = gamemode;
    }

    public int getCurrentTime() {
        return CURRENT_TIME;
    }

    public ShatteredGameMode getGamemode() {
        return GAMEMODE;
    }
}
