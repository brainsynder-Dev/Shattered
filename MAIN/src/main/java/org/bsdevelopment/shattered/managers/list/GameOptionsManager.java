package org.bsdevelopment.shattered.managers.list;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.option.BooleanOption;
import org.bsdevelopment.shattered.option.GameModeOption;
import org.bsdevelopment.shattered.option.Option;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameOptionsManager implements IManager {
    private final Shattered PLUGIN;
    private final Map<String, List<Option>> OPTIONS;

    public Option<ShatteredGameMode> GAMEMODES;

    public Option<Boolean> DISABLE_FALL_DAMAGE;
    public Option<Boolean> GOLDEN_BOW;

    public Option<Integer> BOW_SPAWN_MIN;
    public Option<Integer> BOW_SPAWN_MAX;

    public Option<Integer> BOW_USE_MULTIPLIER;


    public GameOptionsManager(Shattered plugin) {
        PLUGIN = plugin;
        OPTIONS = new HashMap<>();
    }

    @Override
    public void load() {
        register(getClass(), GAMEMODES = new GameModeOption("Gamemode Selection")
                .setDescription("What gamemode should be played"));

        register(getClass(), DISABLE_FALL_DAMAGE = new BooleanOption("No Fall", false)
                .setDescription("Should fall damage be disabled"));
        register(getClass(), GOLDEN_BOW = new BooleanOption("Golden Bow", true)
                .setDescription("When a player gets damaged by another player should it be an Insta-Kill"));

        register(getClass(), BOW_SPAWN_MIN = new Option<>("Bow Spawn Minimum", 20, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110)
                .setDescription("The fastest a bow can spawn in a game"));
        register(getClass(), BOW_SPAWN_MAX = new Option<>("Bow Spawn Maximum", 50, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140)
                .setDescription("The slowest a bow can spawn in a game"));

        register(getClass(), BOW_USE_MULTIPLIER = new Option<>("Bow Use Multiplier", 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .setDescription("Add more uses to special bows"));
    }

    @Override
    public void cleanup() {
        OPTIONS.clear();
    }

    /**
     * If the caller's canonical name is not in the map, add it with an empty list. Then add the option to the list.
     *
     * NOTE: If the option is for a specific gamemode then the 'caller' MUST be that gamemodes class
     *
     * @param caller The class that is calling the register method.
     * @param option The option to register.
     */
    public void register (Class<?> caller, Option option) {
        List<Option> list = OPTIONS.getOrDefault(caller.getCanonicalName(), new ArrayList<>());
        list.add(option);
        OPTIONS.put(caller.getCanonicalName(), list);
    }

    /**
     * > This function removes the statsOption from the STATS array
     *
     * @param statsOption The StatsOption object that you want to unregister.
     */
    public void unregister (Option statsOption) {
        OPTIONS.remove(statsOption);
    }

    @Nullable
    public List<Option> getStatsForCaller (Class<?> target) {
        return OPTIONS.getOrDefault(target.getCanonicalName(), new ArrayList<>());
    }
}
