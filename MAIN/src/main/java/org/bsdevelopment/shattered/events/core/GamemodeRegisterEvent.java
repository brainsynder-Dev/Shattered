package org.bsdevelopment.shattered.events.core;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

public class GamemodeRegisterEvent extends ShatteredEvent {
    private final Culprit CULPRIT;
    private final ShatteredGameMode GAMEMODE;

    public GamemodeRegisterEvent(Culprit culprit, ShatteredGameMode gameMode) {
        CULPRIT = culprit;
        this.GAMEMODE = gameMode;
    }

    public ShatteredGameMode getGamemode() {
        return GAMEMODE;
    }

    public Culprit getCulprit() {
        return CULPRIT;
    }

    public enum Type {
        PLUGIN,
        ADDON
    }

    public static record Culprit (Type type, String name) {}
}
