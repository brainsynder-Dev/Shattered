package org.bsdevelopment.shattered.managers.list;

import com.google.common.collect.Lists;
import com.jeff_media.morepersistentdatatypes.DataType;
import lib.brainsynder.utils.Cuboid;
import net.md_5.bungee.api.ChatColor;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.managers.Management;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LobbyManager implements IManager {
    private final Shattered PLUGIN;
    private final List<Location> READY_DOOR_1;
    private final List<Location> READY_DOOR_2;

    private Location lobbySpawn = null;
    private Cuboid readyCube1 = null, readyCube2 = null;
    private Sign readySign1 = null, readySign2 = null;

    public LobbyManager(Shattered plugin) {
        PLUGIN = plugin;
        READY_DOOR_1 = new ArrayList<>();
        READY_DOOR_2 = new ArrayList<>();
    }

    @Override
    public void load() {
        DataStorage storage = PLUGIN.getDataStorage();
        lobbySpawn = storage.getLocation("lobby-spawn", null);

        if (storage.hasKey("readyCube1")) {
            readyCube1 = new Cuboid(storage.getCompoundTag("readyCube1"));
            List<Location> wallLocations = new ArrayList<>();
            for (Block block : readyCube1.getBlocks()) {
                if (block.getType().name().contains("SIGN") && (readySign1 == null)) {
                    toggleSign(readySign1 = (Sign) block.getState(), false);
                    break;
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Block block : readyCube1.getBlocks()) {
                        if (block.getType() != Material.AIR) continue;

                        int sides = 0;

                        // Checking if the block is on the edge of the region.
                        for (BlockFace face : Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
                            if (!readyCube1.contains(block.getRelative(face))) sides++;
                        }

                        if (sides == 1) wallLocations.add(block.getLocation());
                    }

                    READY_DOOR_1.addAll(wallLocations);
                }
            }.runTaskLater(PLUGIN, 2);
        }
        if (storage.hasKey("readyCube2")) {
            readyCube2 = new Cuboid(storage.getCompoundTag("readyCube2"));
            List<Location> wallLocations = new ArrayList<>();
            for (Block block : readyCube2.getBlocks()) {
                if (block.getType().name().contains("SIGN") && (readySign2 == null)) {
                    toggleSign(readySign2 = (Sign) block.getState(), false);
                    break;
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Block block : readyCube2.getBlocks()) {
                        if (block.getType() != Material.AIR) continue;

                        int sides = 0;

                        // Checking if the block is on the edge of the region.
                        for (BlockFace face : Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {
                            if (!readyCube2.contains(block.getRelative(face))) sides++;
                        }

                        if (sides == 1) wallLocations.add(block.getLocation());
                    }

                    READY_DOOR_2.addAll(wallLocations);
                }
            }.runTaskLater(PLUGIN, 2);
        }
    }

    public void toggleSign(Sign sign, boolean toggle) {
        sign.setLine(0, ChatColor.GRAY + "Ready Check");
        sign.setLine(1, ChatColor.DARK_GRAY + "===============");
        sign.setLine(2, toggle ? (ChatColor.GREEN + "READY") : (ChatColor.RED + "NOT READY"));
        sign.setLine(3, ChatColor.DARK_GRAY + "===============");

        sign.getPersistentDataContainer().set(Management.KEY_MANAGER.READY_SIGN_KEY, DataType.BOOLEAN, toggle);

        sign.update();
    }

    public void toggleDoor (List<Location> locations, boolean toggle) {
        if (toggle) {
            PLUGIN.getSchematics().setBlocks(locations, Material.GRAY_STAINED_GLASS, () -> {});
        }else{
            PLUGIN.getSchematics().setBlocks(locations, Material.AIR, () -> {});
        }
    }

    @Override
    public void cleanup() {
        updateDataStorage();

        lobbySpawn = null;
    }

    public void joinLobby(ShatteredPlayer shatteredPlayer) {
        shatteredPlayer.setSpectating(false);
        shatteredPlayer.setPlaying(false);
        shatteredPlayer.setState(ShatteredPlayer.PlayerState.LOBBY);

        shatteredPlayer.fetchPlayer(player -> {
            player.setArrowsInBody(0);
            player.teleport(lobbySpawn);
        });
    }

    public List<Location> getReadyDoor1() {
        return READY_DOOR_1;
    }

    public List<Location> getReadyDoor2() {
        return READY_DOOR_2;
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

    public Sign getReadySign1() {
        return readySign1;
    }

    public Sign getReadySign2() {
        return readySign2;
    }

    public void setReadyCube1(Cuboid readyCube) {
        this.readyCube1 = readyCube;
        updateDataStorage();
    }

    public void setReadyCube2(Cuboid readyCube) {
        this.readyCube2 = readyCube;
        updateDataStorage();
    }

    public void setLobbySpawn(Location location) {
        lobbySpawn = location;
        updateDataStorage();
    }

    private void updateDataStorage() {
        DataStorage storage = PLUGIN.getDataStorage();
        if (lobbySpawn != null) storage.setLocation("lobby-spawn", lobbySpawn);
        if (readyCube1 != null) storage.setTag("readyCube1", readyCube1.serialize());
        if (readyCube2 != null) storage.setTag("readyCube2", readyCube2.serialize());
        storage.save();
    }
}
