package org.bsdevelopment.shattered.managers.list;

import lib.brainsynder.nbt.StorageTagList;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.option.StatsOption;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GameStatsManager implements IManager {
    private final Shattered PLUGIN;
    private final List<StatsOption> STATS;

    private StorageTagList PREVIOUS_STATS;

    public StatsOption DEATHS;
    public StatsOption BLOCKS_BROKEN;
    public StatsOption WALLS_BUILT;
    public StatsOption PATCHED_BLOCKS;
    public StatsOption FRUITY_CLEANUPS;
    public StatsOption PLAYERS_INTOXICATED;

    public GameStatsManager(Shattered plugin) {
        PLUGIN = plugin;
        STATS = new ArrayList<>();
        PREVIOUS_STATS = new StorageTagList();
    }

    @Override
    public void load() {
        register(DEATHS = new StatsOption("deaths", "Player Reconstruction Surgeries", "How many player deaths were there?"));
        register(BLOCKS_BROKEN = new StatsOption("Glass Shattered", "How many Glass blocks broken/shattered?"));
        register(WALLS_BUILT = new StatsOption("Walls Built", "How many walls were built?"));
        register(PATCHED_BLOCKS = new StatsOption("fixed_blocks", "Patched Blocks", "How many blocks were fixed?"));
        register(FRUITY_CLEANUPS = new StatsOption("fruits_shot", "Fruity Cleanups", "How many Fruits were popped?"));
        register(PLAYERS_INTOXICATED = new StatsOption("intoxications", "Players Intoxicated", "How many players were hit by the 'Drunker' bow?"));

        DataStorage storage = PLUGIN.getDataStorage();
        if (storage.hasKey("previous-game-stats"))
            PREVIOUS_STATS = (StorageTagList) storage.getTag("previous-game-stats");
    }

    @Override
    public void cleanup() {
        STATS.clear();

        DEATHS = null;
        BLOCKS_BROKEN = null;
        WALLS_BUILT = null;
        PATCHED_BLOCKS = null;
        FRUITY_CLEANUPS = null;
        PLAYERS_INTOXICATED = null;
    }

    /**
     * Register a new stat, so it can be displayed when the game end
     * It also allows it to get reset at the end of the game.
     *
     * @param statsOption The StatsOption object that you want to register.
     */
    public void register (StatsOption statsOption) {
        STATS.add(statsOption);
    }

    /**
     * > This function removes the statsOption from the STATS array
     *
     * @param statsOption The StatsOption object that you want to unregister.
     */
    public void unregister (StatsOption statsOption) {
        STATS.remove(statsOption);
    }

    /**
     * Saves the current stats, then set all the stats back to 0.
     */
    public void resetStats () {
        saveStats();

        STATS.forEach(statsOption -> statsOption.setValue(0));
    }

    private void saveStats () {
        PREVIOUS_STATS = new StorageTagList();

        STATS.forEach(statsOption -> {
            PREVIOUS_STATS.appendTag(statsOption.toCompound());
        });

        PLUGIN.getDataStorage().setTag("previous-game-stats", PREVIOUS_STATS);
        PLUGIN.getDataStorage().save();
    }

    public StorageTagList getCurrentStats() {
        StorageTagList list = new StorageTagList();

        STATS.forEach(statsOption -> {
            list.appendTag(statsOption.toCompound());
        });
        return list;
    }

    /**
     * This function returns the previous games stats.
     *
     * @return The value of the variable PREVIOUS_STATS.
     */
    public StorageTagList getPreviousStats() {
        if (PREVIOUS_STATS.getList().isEmpty()) return getCurrentStats();
        return PREVIOUS_STATS;
    }

    /**
     * Searches for a stat by either its key, name, or storage format name (lowercase with '_')
     */
    @Nullable
    public StatsOption searchForStat (String search) {
        for (StatsOption stat : STATS) {
            if (stat.getKey().equals(search)) return stat;
            if (stat.getStorageName().equals(search)) return stat;
            if (stat.getName().equalsIgnoreCase(search)) return stat;
        }

        return null;
    }
}
