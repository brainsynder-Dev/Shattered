package org.bsdevelopment.shattered.managers.list;

import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.utils.DyeColorWrapper;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class GlassManager implements IManager {
    private final Map<DyeColorWrapper, Object> BREAK_ORDER_MAP;
    private final Map<Location, BlockState> ORIGINAL_STATE_MAP;
    private final Shattered PLUGIN;

    public GlassManager(Shattered plugin) {
        PLUGIN = plugin;
        BREAK_ORDER_MAP = new HashMap<>();
        ORIGINAL_STATE_MAP = new HashMap<>();
    }

    @Override
    public void load() {
        BREAK_ORDER_MAP.put(DyeColorWrapper.BLACK, DyeColorWrapper.GRAY);
        BREAK_ORDER_MAP.put(DyeColorWrapper.GRAY, DyeColorWrapper.LIGHT_GRAY);
        BREAK_ORDER_MAP.put(DyeColorWrapper.LIGHT_GRAY, DyeColorWrapper.WHITE);
        BREAK_ORDER_MAP.put(DyeColorWrapper.WHITE, null);


        DataStorage storage = PLUGIN.getDataStorage();
        if (!storage.hasKey("glass-reset-list")) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Clearing old glass blocks...");

                StorageTagList list = (StorageTagList) storage.getTag("glass-reset-list");
                list.getList().forEach(base -> {
                    StorageTagCompound compound = (StorageTagCompound) base;
                    Location location = new Location(Bukkit.getWorld(compound.getString("world")), compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
                    Block block = location.getBlock();
                    block.setType(Material.AIR);
                    block.getState().update();
                });


                storage.remove("glass-reset-list");
                storage.save();
            }
        }.runTaskLater(PLUGIN, 30);
    }

    @Override
    public void cleanup() {
        StorageTagList list = new StorageTagList();
        ORIGINAL_STATE_MAP.forEach((location, blockState) -> {
            StorageTagCompound compound = new StorageTagCompound();
            compound.setString("world", location.getWorld().getName());
            compound.setDouble("x", location.getX());
            compound.setDouble("y", location.getY());
            compound.setDouble("z", location.getZ());
            list.appendTag(compound);
        });

        PLUGIN.getDataStorage().setTag("glass-reset-list", list);
        PLUGIN.getDataStorage().save();

        BREAK_ORDER_MAP.clear();
        ORIGINAL_STATE_MAP.clear();
    }

    public void handleGlass(Block block) {
        if (!block.getType().name().contains("GLASS")) return;
        Management.GAME_STATS_MANAGER.BLOCKS_BROKEN.increase();

        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
        block.getWorld().spawnParticle(Particle.BLOCK_CRACK, block.getLocation().add(0.5, 0, 0.5),
                50, 0.5, 0.5, 0.5, 0, block.getBlockData());

        if (!isSaved(block)) saveBlock(block);


        if ((block.getType() == Material.GLASS)
                || (block.getType() == Material.GLASS_PANE)) {
                // TODO: || Options.FRAGILE_GLASS.getValue()) {
            block.setType(Material.AIR);
            return;
        }

        degradeGlass(block);
    }

    private void degradeGlass(Block block) {
        if (!isSaved(block)) return;
        Material material = block.getType();

        String type = "_STAINED_GLASS";
        if (material.name().contains("PANE")) {
            type = "_STAINED_GLASS_PANE";
        }

        Object color = BREAK_ORDER_MAP.getOrDefault(DyeColorWrapper.getByName(material.name().replace(type, "")), "DEFAULT");
        if (color == null) {
            if (material.name().contains("PANE")) {
                block.setType(Material.GLASS_PANE);
                return;
            }
            block.setType(Material.GLASS);
        } else if (color.equals("DEFAULT")) {
            block.setType(Material.valueOf("BLACK" + type));
        } else {
            block.setType(Material.valueOf(color + type));
        }
        block.getState().update();
    }




    public void saveBlock(Block block) {
        ORIGINAL_STATE_MAP.put(block.getLocation(), block.getState());
    }

    public boolean isSaved(Block block) {
        return ORIGINAL_STATE_MAP.containsKey(block.getLocation());
    }

    public void resetBlocks() {
        if (ORIGINAL_STATE_MAP.isEmpty()) return;
        ORIGINAL_STATE_MAP.forEach((location, blockState) -> {
            if (blockState.getType() == Material.AIR) blockState.update(true);
        });
        ORIGINAL_STATE_MAP.clear();
    }

    public void reset(Location location) {
        if (!ORIGINAL_STATE_MAP.containsKey(location)) return;
        ORIGINAL_STATE_MAP.get(location).update(true);
        ORIGINAL_STATE_MAP.remove(location);
    }
}
