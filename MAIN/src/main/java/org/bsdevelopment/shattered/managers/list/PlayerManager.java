package org.bsdevelopment.shattered.managers.list;

import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.IManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements IManager {
    private final Shattered PLUGIN;
    private final Map<UUID, ShatteredPlayer> PLAYER_MAP;

    public PlayerManager(Shattered plugin) {
        PLUGIN = plugin;
        PLAYER_MAP = new HashMap<>();
    }

    /**
     * If the player is not in the map, add them to the map and return the new ShatteredPlayer
     *
     * @param player The player to get the ShatteredPlayer of.
     * @return A ShatteredPlayer object.
     */
    public ShatteredPlayer getShatteredPlayer (Player player) {
        return PLAYER_MAP.computeIfAbsent(player.getUniqueId(), uuid -> new ShatteredPlayer(player));
    }

    /**
     * Get a ShatteredPlayer object from a player's name.
     *
     * @param name The name of the player you want to get the ShatteredPlayer object of.
     * @return A ShatteredPlayer object
     */
    public ShatteredPlayer getShatteredPlayer (String name) {
        for (ShatteredPlayer player : PLAYER_MAP.values()) {
            if (name.equalsIgnoreCase(player.getName())) return player;
        }
        return null;
    }

    @Override
    public void load() {
        DataStorage storage = PLUGIN.getDataStorage();

        if (!storage.hasKey("player-storage")) return;
        StorageTagList list = (StorageTagList) storage.getTag("player-storage");
        list.getList().forEach(base -> {
            StorageTagCompound compound = (StorageTagCompound) base;
            ShatteredPlayer player = new ShatteredPlayer(compound);
            PLAYER_MAP.put(player.getUuid(), player);
        });
    }

    @Override
    public void cleanup() {
        DataStorage storage = PLUGIN.getDataStorage();

        StorageTagList list = new StorageTagList();
        PLAYER_MAP.forEach((uuid, shatteredPlayer) -> {
            list.appendTag(shatteredPlayer.toCompound());
        });
        storage.setTag("player-storage", list);
        storage.save();

        PLAYER_MAP.clear();
    }


}
