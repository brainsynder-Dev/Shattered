package org.bsdevelopment.shattered.managers.list;

import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.IManager;
import org.bukkit.Location;

public class LobbyManager implements IManager {
    private Location lobbySpawn = null;
    private Cuboid readyCube1 = null, readyCube2 = null;

    private final Shattered PLUGIN;

    public LobbyManager(Shattered plugin) {
        PLUGIN = plugin;
    }

    @Override
    public void load() {
        DataStorage storage = PLUGIN.getDataStorage();
        lobbySpawn = storage.getLocation("lobby-spawn", null);

        if (storage.hasKey("readyCube1")) readyCube1 = new Cuboid (storage.getCompoundTag("readyCube1"));
        if (storage.hasKey("readyCube2")) readyCube2 = new Cuboid (storage.getCompoundTag("readyCube2"));
    }

    @Override
    public void cleanup() {
        updateDataStorage();

        lobbySpawn = null;
    }

    public void joinLobby(ShatteredPlayer shatteredPlayer) {
        shatteredPlayer.setSpectating(false);
        shatteredPlayer.setPlaying(false);

        shatteredPlayer.fetchPlayer(player -> {
            player.teleport(lobbySpawn);
        });
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public Cuboid getReadyCube1() {
        return readyCube1;
    }

    public Cuboid getReadyCube2() {
        return readyCube2;
    }

    public void setReadyCube1(Cuboid readyCube) {
        this.readyCube1 = readyCube;
        updateDataStorage();
    }

    public void setReadyCube2(Cuboid readyCube) {
        this.readyCube2 = readyCube;
        updateDataStorage();
    }

    public void setLobbySpawn (Location location) {
        lobbySpawn = location;
        updateDataStorage();
    }

    private void updateDataStorage () {
        DataStorage storage = PLUGIN.getDataStorage();
        if (lobbySpawn != null) storage.setLocation("lobby-spawn", lobbySpawn);
        if (readyCube1 != null) storage.setTag("readyCube1", readyCube1.serialize());
        if (readyCube2 != null) storage.setTag("readyCube2", readyCube2.serialize());
        storage.save();
    }
}
