package org.bsdevelopment.shattered.events.lobby;

import org.bsdevelopment.shattered.events.ShatteredCancelEvent;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.option.Option;

public class GameOptionChangeEvent extends ShatteredCancelEvent {
    private final ShatteredPlayer SHATTERED_PLAYER;
    private final Option<?> OPTION;

    public GameOptionChangeEvent(ShatteredPlayer shattered_player, Option<?> option) {
        SHATTERED_PLAYER = shattered_player;
        OPTION = option;
    }

    public Option<?> getOption() {
        return OPTION;
    }

    public ShatteredPlayer getShatteredPlayer() {
        return SHATTERED_PLAYER;
    }
}
