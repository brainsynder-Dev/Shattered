package org.bsdevelopment.shattered.managers.list;

import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.utilities.ReadyCube;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class LobbyManager implements IManager {
    private final Shattered PLUGIN;
    private final List<ShatteredPlayer> PLAYERS;

    private Location lobbySpawn = null;
    private ReadyCube readyCube1 = null;
    private ReadyCube readyCube2 = null;

    public LobbyManager(Shattered plugin) {
        PLUGIN = plugin;

        PLAYERS = new ArrayList<>();
    }

    @Override
    public void load() {
        DataStorage storage = PLUGIN.getDataStorage();
        lobbySpawn = storage.getLocation("lobby-spawn", null);

        if (storage.hasKey("readyCube1")) {
            readyCube1 = new ReadyCube(storage.getCompoundTag("readyCube1"));
        }
        if (storage.hasKey("readyCube2")) {
            readyCube2 = new ReadyCube(storage.getCompoundTag("readyCube2"));
        }
    }

    @Override
    public void cleanup() {
        updateDataStorage();

        PLAYERS.clear();

        lobbySpawn = null;
    }

    public void joinLobby(ShatteredPlayer shatteredPlayer) {
        if (!PLAYERS.contains(shatteredPlayer)) PLAYERS.add(shatteredPlayer);

        shatteredPlayer.setSpectating(false);
        shatteredPlayer.setPlaying(false);
        shatteredPlayer.setState(ShatteredPlayer.PlayerState.LOBBY);

        shatteredPlayer.fetchPlayer(player -> {
            player.setArrowsInBody(0);
            player.teleport(lobbySpawn);
        });
    }

    public void leaveLobby (ShatteredPlayer shatteredPlayer) {
        PLAYERS.remove(shatteredPlayer);

        shatteredPlayer.setSpectating(false);
        shatteredPlayer.setPlaying(false);
        shatteredPlayer.setState(ShatteredPlayer.PlayerState.UNKNOWN);
    }

    public List<ShatteredPlayer> getLobbyPlayers() {
        return PLAYERS;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public ReadyCube getReadyCube1() {
        return readyCube1;
    }

    public ReadyCube getReadyCube2() {
        return readyCube2;
    }

    public void setReadyCube1(Cuboid readyCube) {
        this.readyCube1 = new ReadyCube (readyCube);
        updateDataStorage();
    }

    public void setReadyCube2(Cuboid readyCube) {
        this.readyCube2 = new ReadyCube (readyCube);
        updateDataStorage();
    }

    public void setLobbySpawn(Location location) {
        lobbySpawn = location;
        updateDataStorage();
    }

    private void updateDataStorage() {
        DataStorage storage = PLUGIN.getDataStorage();
        if (lobbySpawn != null) storage.setLocation("lobby-spawn", lobbySpawn);
        if (readyCube1 != null) storage.setTag("readyCube1", readyCube1.toCompound());
        if (readyCube2 != null) storage.setTag("readyCube2", readyCube2.toCompound());
        storage.save();
    }
}
