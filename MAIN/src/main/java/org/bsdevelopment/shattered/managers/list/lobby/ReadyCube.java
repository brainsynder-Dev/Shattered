package org.bsdevelopment.shattered.managers.list.lobby;

import com.google.common.collect.Lists;
import com.jeff_media.morepersistentdatatypes.DataType;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.utils.BlockLocation;
import lib.brainsynder.utils.Cuboid;
import net.md_5.bungee.api.ChatColor;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ReadyCube {
    private final LobbyManager LOBBY_MANAGER;
    private final Cuboid REGION;
    private final List<Location> DOOR = new ArrayList<>();
    private final org.bukkit.ChatColor COLOR;

    private Sign readySign;

    public ReadyCube(LobbyManager lobby_manager, org.bukkit.ChatColor color, StorageTagCompound compound) {
        LOBBY_MANAGER = lobby_manager;
        REGION = new Cuboid(compound.getCompoundTag("region"));
        COLOR = color;

        StorageTagList list = (StorageTagList) compound.getTag("door");
        list.getList().forEach(base -> DOOR.add(BlockLocation.fromCompound((StorageTagCompound) base).toLocation()));

        initiate(false);
    }

    public ReadyCube(LobbyManager lobby_manager, org.bukkit.ChatColor color, Cuboid region) {
        LOBBY_MANAGER = lobby_manager;
        this.REGION = region;
        COLOR = color;

        initiate(true);
    }

    private void initiate (boolean firstLoad) {
        for (Block block : REGION.getBlocks()) {
            if (block.getType().name().contains("SIGN") && (readySign == null)) {
                readySign = (Sign) block.getState();
                break;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (DOOR.isEmpty()) {
                    for (Block block : REGION.getBlocks()) {
                        if (block.getType() != Material.AIR) continue;

                        int sides = 0;

                        // Checking if the block is on the edge of the region.
                        for (BlockFace face : Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
                            if (!REGION.contains(block.getRelative(face))) sides++;
                        }

                        if (sides == 1) DOOR.add(block.getLocation());
                    }
                }

                if (!firstLoad) {
                    toggleCube(false);
                }else{
                    toggleSign(readySign, false);

                    REGION.getWorld().getNearbyEntities(REGION.getCenter(), 100, 100, 100, entity -> entity instanceof Player).forEach(entity -> {
                        renderCube((Player) entity);
                    });
                }

                LOBBY_MANAGER.updateDataStorage();
            }
        }.runTaskLater(Shattered.INSTANCE, 2);
    }

    public void renderCube (Player player) {
        List<Location> locations = new ArrayList<>();
        for (Block block : REGION.getBlocks()) {
            int sides = 0;

            // Checking if the block is on the edge of the region.
            for (BlockFace face : Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
                if (!REGION.contains(block.getRelative(face))) sides++;
            }

            if (sides >= 2) locations.add(block.getLocation());
        }

        locations.forEach(location -> {
            ShatteredUtilities.highlightBlock(COLOR, location, 20 * 30, player);
        });

        DOOR.forEach(location -> {
            ShatteredUtilities.highlightBlock(org.bukkit.ChatColor.RED, location, 20 * 30, player);
        });

        if (readySign != null) {
            ShatteredUtilities.highlightBlock(org.bukkit.ChatColor.GOLD, readySign.getLocation(), 20 * 30, player);
        }
    }

    /**
     * This function converts the object into a StorageTagCompound.
     *
     * @return A StorageTagCompound
     */
    public StorageTagCompound toCompound () {
        StorageTagCompound compound = new StorageTagCompound();
        compound.setTag("region", REGION.serialize());

        StorageTagList list = new StorageTagList();
        DOOR.forEach(location -> {
            list.appendTag(new BlockLocation(location).toCompound());
        });
        compound.setTag("door", list);
        return compound;
    }

    /**
     * It sets the lines of the sign to the appropriate text, and then sets the sign's persistent data to the boolean value
     * of the toggle parameter
     *
     * @param sign The sign to toggle
     * @param toggle Whether or not the sign should be toggled to ready or not ready.
     */
    private void toggleSign(Sign sign, boolean toggle) {
        sign.setLine(0, ChatColor.GRAY + "Ready Check");
        sign.setLine(1, ChatColor.DARK_GRAY + "===============");
        sign.setLine(2, toggle ? (ChatColor.GREEN + "READY") : (ChatColor.RED + "NOT READY"));
        sign.setLine(3, ChatColor.DARK_GRAY + "===============");

        sign.getPersistentDataContainer().set(Management.KEY_MANAGER.READY_SIGN_KEY, DataType.BOOLEAN, toggle);

        sign.update();
    }

    /**
     * If the boolean is true, set the blocks in the door region to gray stained glass, otherwise set them to air
     *
     * @param toggle Whether or not to toggle the sign.
     */
    public void toggleCube(boolean toggle) {
        toggleSign(readySign, toggle);

        if (toggle) {
            Shattered.INSTANCE.getSchematics().setBlocks(DOOR, Material.GRAY_STAINED_GLASS, () -> {});
        }else{
            Shattered.INSTANCE.getSchematics().setBlocks(DOOR, Material.AIR, () -> {});
        }
    }

    public List<ShatteredPlayer> getCubePlayers () {
        List<ShatteredPlayer> list = new ArrayList<>();
        REGION.getEntities().stream().filter(entity -> entity instanceof Player).forEach(entity -> {
            list.add(Management.PLAYER_MANAGER.getShatteredPlayer((Player) entity));
        });
        return list;
    }

    /**
     * If the sign has a boolean value for the key "ready", return that value. Otherwise, return false
     *
     * @return A boolean value.
     */
    public boolean isReady () {
        if (!readySign.getPersistentDataContainer().has(Management.KEY_MANAGER.READY_SIGN_KEY, DataType.BOOLEAN)) return false;
        return readySign.getPersistentDataContainer().get(Management.KEY_MANAGER.READY_SIGN_KEY, DataType.BOOLEAN);
    }

    /**
     * This function returns the region of the cube.
     *
     * @return The region of the cuboid.
     */
    public Cuboid getRegion() {
        return REGION;
    }

    /**
     * This function returns a list of locations that represent the door.
     *
     * @return A list of locations.
     */
    public List<Location> getDoor() {
        return DOOR;
    }

    /**
     * This function returns the readySign variable.
     *
     * @return The readySign variable is being returned.
     */
    public Sign getReadySign() {
        return readySign;
    }
}
