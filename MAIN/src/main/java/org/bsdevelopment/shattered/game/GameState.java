package org.bsdevelopment.shattered.game;

public enum GameState {
    WAITING,
    READY_UP(WAITING),
    COUNTDOWN(WAITING),
    IN_GAME,
    CLEANUP;

    private final GameState overall;

    GameState () {
        this.overall = this;
    }
    GameState (GameState overall) {
        this.overall = overall;
    }

    public GameState getOverall() {
        return overall;
    }
}
