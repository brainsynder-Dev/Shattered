package org.bsdevelopment.shattered.managers.list;

import com.google.common.collect.Lists;
import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.options.ConfigOption;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.RandomCollection;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Collection;

public class ArenaManager implements IManager {
    private final RandomCollection<Location> BOW_SPAWNS;
    private final RandomCollection<Location> SPAWNABLE_BLOCKS;
    private final RandomCollection<Location> PURPLE_SPAWNS;
    private final RandomCollection<Location> GREEN_SPAWNS;

    private Cuboid region;

    public ArenaManager() {
        BOW_SPAWNS = new RandomCollection<>();
        SPAWNABLE_BLOCKS = new RandomCollection<>();
        PURPLE_SPAWNS = new RandomCollection<>();
        GREEN_SPAWNS = new RandomCollection<>();
    }

    public void fromSchematicRegion(Cuboid region) {
        this.region = region;

        if (region == null) return;

        for (Block block : region.getBlocks()) {
            if (block == null) continue;
            // Checking if the block is purple wool, and if the block above it is air. If both of these are true, it adds
            // the location of the block to the PURPLE_SPAWNS list.
            if ((block.getType() == Material.PURPLE_WOOL) && (block.getRelative(BlockFace.UP).getType() == Material.AIR)) {
                PURPLE_SPAWNS.add(50, block.getLocation().add(0.5, 1.5, 0.5));
                continue;
            }

            // Checking if the block is green wool, and if the block above it is air. If both of these are true, it adds
            // the location of the block to the GREEN_SPAWNS list.
            if ((block.getType() == Material.LIME_WOOL) && (block.getRelative(BlockFace.UP).getType() == Material.AIR)) {
                GREEN_SPAWNS.add(50, block.getLocation().add(0.5, 1.5, 0.5));
                continue;
            }

            // This is checking if the block is a sea lantern, and if it is, it checks if the block has quartz around it.
            // If both of these are true, it adds the location of the block to the BOW_SPAWNS list.
            if (block.getType() != Material.SEA_LANTERN) continue;
            if (!hasQuartz(block)) continue;

            BOW_SPAWNS.add(block.getLocation().add(0.5, 1, 0.5));
        }

        // Searching for all blocks that are either quartz blocks, quartz bricks, quartz pillars, or smooth quartz.
        for (Block block : ShatteredUtilities.search(region, Lists.newArrayList(
                Material.QUARTZ_BLOCK, Material.QUARTZ_BRICKS, Material.QUARTZ_PILLAR, Material.SMOOTH_QUARTZ))) {
            Block top = block.getRelative(BlockFace.UP);

            Location target = null;

            // Checking if the block above the current block is air, if it is, it adds the location of the block to the
            // SPAWNABLE_BLOCKS list.
            if (top.getType() == Material.AIR) {
                target = top.getLocation().add(0.5, 0.5, 0.5);
                if (checkAreaThreshold(target.clone().subtract(0,1,0), false)) SPAWNABLE_BLOCKS.add(target);
                continue;
            }

            // Checking if the block above the current block is glass, if it is, it checks if the block above the glass is
            // air, if it is, it adds the location of the block above the glass to the SPAWNABLE_BLOCKS list.
            if (top.getType().name().contains("GLASS")) {
                if (top.getRelative(BlockFace.UP).getType() == Material.AIR) {
                    target = top.getRelative(BlockFace.UP).getLocation().add(0.5, 0.5, 0.5);
                    if (checkAreaThreshold(target.clone().subtract(0,1,0), false)) SPAWNABLE_BLOCKS.add(target);
                }
                continue;
            }

            // Checking if the block above the current block is air, if it is not, it sets the current block to the block
            // above it.
            while (top.getType() != Material.AIR) top = top.getRelative(BlockFace.UP);

            // Checking if the block below the current block is not solid, and if the block above the current block is air.
            // If both of these are true, it adds the location of the current block to the SPAWNABLE_BLOCKS list.
            if ((!top.getRelative(BlockFace.DOWN).getType().isSolid()) && (top.getRelative(BlockFace.UP).getType() == Material.AIR)) {
                target = top.getLocation().add(0.5, 0.5, 0.5);
                if (checkAreaThreshold(target.clone().subtract(0,1,0), false)) SPAWNABLE_BLOCKS.add(target);
            }
        }
    }

    /**
     * If the block is surrounded by solid blocks on at least 3 sides (default), and the block above it is not solid, then return
     * true.
     *
     * @param location The location of the block to check.
     * @return A boolean value.
     */
    public boolean checkAreaThreshold(Location location, boolean debug) {
        int sides = 0;
        int threshold = ConfigOption.INSTANCE.SPAWN_THRESHOLD.getValue();

        // Checking if the block is surrounded by solid blocks.
        for (BlockFace face : Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {
            if (checkBlock(location.getBlock().getRelative(face))) sides++;
        }

        if (sides >= threshold) {
            if (debug) Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Threshold ("+threshold+")  Sides ("+sides+")   Check Value: "+(sides >= threshold));
            return !checkBlock(location.add(0,2,0).getBlock());
        }

        return false;
    }

    /**
     * If the block is air, return false, otherwise return true if the block is solid.
     *
     * @param block The block to check
     * @return A boolean value.
     */
    private boolean checkBlock(Block block) {
        Material material = block.getType();
        if (material == Material.AIR) return false;
        return material.isSolid();
    }

    /**
     * This function returns the current maps' region.
     *
     * @return The region of the cuboid.
     */
    public Cuboid getRegion() {
        return region;
    }

    /**
     * It returns a list of locations that are the bow spawns
     *
     * @return A list of locations
     */
    public Collection<Location> getBowSpawns() {
        return getRandomBowSpawns().values();
    }

    public RandomCollection<Location> getRandomBowSpawns() {
        return BOW_SPAWNS;
    }

    /**
     * This function returns the green spawns.
     *
     * @return A RandomCollection of Location objects.
     */
    public RandomCollection<Location> getGreenSpawns() {
        return GREEN_SPAWNS;
    }

    /**
     * It returns a random location from the list of purple spawns
     *
     * @return A RandomCollection of Location objects.
     */
    public RandomCollection<Location> getPurpleSpawns() {
        return PURPLE_SPAWNS;
    }

    /**
     * Returns a RandomCollection of all the blocks that can be spawned on.
     *
     * @return A RandomCollection of Location objects.
     */
    public RandomCollection<Location> getSpawnableBlocks() {
        return SPAWNABLE_BLOCKS;
    }

    @Override
    public void load() {

    }

    @Override
    public void cleanup() {
        region = null;

        BOW_SPAWNS.clear();

        SPAWNABLE_BLOCKS.clear();
        GREEN_SPAWNS.clear();
        PURPLE_SPAWNS.clear();
    }

    /**
     * "If the block has a quartz pillar on each side, return true, otherwise return false."
     * <p>
     * Pattern:
     * ░P░
     * PSP
     * ░P░
     * <p>
     * Key:
     * ░ = Either AIR or another block
     * P = quartz pillar
     * S = Sea Lantern
     *
     * @param block The block that the player is trying to place.
     * @return A boolean value.
     */
    private boolean hasQuartz(Block block) {
        for (BlockFace face : Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {
            if (block.getRelative(face).getType() != Material.QUARTZ_PILLAR) return false;
        }
        return true;
    }
}
