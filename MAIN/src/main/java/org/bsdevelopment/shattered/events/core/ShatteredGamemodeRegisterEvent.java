package org.bsdevelopment.shattered.events.core;

import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;

/**
 * `GamemodeRegisterEvent` is an event that is called when a gamemode is registered
 */
public class ShatteredGamemodeRegisterEvent extends ShatteredEvent {
    private final Culprit CULPRIT;
    private final ShatteredGameMode GAMEMODE;

    public ShatteredGamemodeRegisterEvent(Culprit culprit, ShatteredGameMode gameMode) {
        CULPRIT = culprit;
        this.GAMEMODE = gameMode;
    }

    /**
     * It returns the gamemode of the game
     *
     * @return The GAMEMODE variable.
     */
    public ShatteredGameMode getGamemode() {
        return GAMEMODE;
    }

    /**
     * This function returns the culprit.
     *
     * @return The culprit is being returned.
     */
    public Culprit getCulprit() {
        return CULPRIT;
    }

    public enum Type {
        PLUGIN,
        ADDON
    }

    public static record Culprit (Type type, String name) {}
}
