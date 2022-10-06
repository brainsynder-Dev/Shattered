package org.bsdevelopment.shattered.option;

import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.game.modes.list.FFAGameMode;
import org.bsdevelopment.shattered.managers.Management;

public class GameModeOption extends Option <ShatteredGameMode>{
    public GameModeOption(String name) {
        super(name, Management.GAME_MANAGER.getGameMode(FFAGameMode.class));
    }
}
