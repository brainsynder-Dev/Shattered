package org.bsdevelopment.shattered.managers.list.lobby;

import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.TimeType;
import org.bukkit.ChatColor;
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
            readyCube1 = new ReadyCube(this, ChatColor.DARK_GREEN, storage.getCompoundTag("readyCube1"));
        }
        if (storage.hasKey("readyCube2")) {
            readyCube2 = new ReadyCube(this, ChatColor.DARK_PURPLE, storage.getCompoundTag("readyCube2"));
        }
    }

    @Override
    public void cleanup() {
        updateDataStorage();

        PLAYERS.clear();

        lobbySpawn = null;
    }

    public void updateLighting(ShatteredPlayer shatteredPlayer) {
        if (Management.GAME_OPTIONS_MANAGER.LIGHTING.getValue() == TimeType.DAY) {
            shatteredPlayer.fetchPlayer(player -> player.setPlayerTime(6000, false));
        } else {
            shatteredPlayer.fetchPlayer(player -> player.setPlayerTime(18000, false));
        }
    }

    public void joinLobby(ShatteredPlayer shatteredPlayer) {
        if (!PLAYERS.contains(shatteredPlayer)) PLAYERS.add(shatteredPlayer);

        shatteredPlayer.setSpectating(false);
        shatteredPlayer.setPlaying(false);
        shatteredPlayer.setState(ShatteredPlayer.PlayerState.LOBBY);
        shatteredPlayer.removeBoard();

        shatteredPlayer.fetchPlayer(player -> {
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            player.setFallDistance(0);
            player.setHealth(20);
            player.setArrowsInBody(0);
            player.teleport(lobbySpawn);

            PLAYERS.forEach(shatteredPlayer1 -> {
                if (shatteredPlayer == shatteredPlayer1) return;
                shatteredPlayer1.fetchPlayer(other -> {
                    other.showPlayer(PLUGIN, player);
                });
            });
        });

        updateLighting(shatteredPlayer);
    }

    public void leaveLobby(ShatteredPlayer shatteredPlayer) {
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
        this.readyCube1 = new ReadyCube(this, ChatColor.DARK_GREEN, readyCube);
    }

    public void setReadyCube2(Cuboid readyCube) {
        this.readyCube2 = new ReadyCube(this, ChatColor.DARK_PURPLE, readyCube);
    }

    public void setLobbySpawn(Location location) {
        lobbySpawn = location;
        updateDataStorage();
    }

    void updateDataStorage() {
        DataStorage storage = PLUGIN.getDataStorage();
        if (lobbySpawn != null) storage.setLocation("lobby-spawn", lobbySpawn);
        if (readyCube1 != null) storage.setTag("readyCube1", readyCube1.toCompound());
        if (readyCube2 != null) storage.setTag("readyCube2", readyCube2.toCompound());
        storage.save();
    }
}
