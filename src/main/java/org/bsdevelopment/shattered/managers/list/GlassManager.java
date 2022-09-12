package org.bsdevelopment.shattered.managers.list;

import lib.brainsynder.utils.DyeColorWrapper;
import org.bsdevelopment.shattered.managers.IManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public class GlassManager implements IManager {
    private final Map<DyeColorWrapper, Object> BREAK_ORDER_MAP;
    private final Map<Location, BlockState> ORIGINAL_STATE_MAP;

    public GlassManager() {
        BREAK_ORDER_MAP = new HashMap<>();
        ORIGINAL_STATE_MAP = new HashMap<>();
    }

    @Override
    public void load() {
        BREAK_ORDER_MAP.put(DyeColorWrapper.BLACK, DyeColorWrapper.GRAY);
        BREAK_ORDER_MAP.put(DyeColorWrapper.GRAY, DyeColorWrapper.LIGHT_GRAY);
        BREAK_ORDER_MAP.put(DyeColorWrapper.LIGHT_GRAY, DyeColorWrapper.WHITE);
        BREAK_ORDER_MAP.put(DyeColorWrapper.WHITE, null);
    }

    @Override
    public void cleanup() {
        BREAK_ORDER_MAP.clear();
        ORIGINAL_STATE_MAP.clear();
    }


    private void degradeGlass(Block block) {
        if (!isSaved(block)) return;
        Material material = block.getType();

        String type = "_STAINED_GLASS";
        if (material.name().contains("PANE")) {
            type = "_STAINED_GLASS_PANE";
        }

        Object color = BREAK_ORDER_MAP.getOrDefault(material.name().replace(type, ""), "DEFAULT");
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
        ORIGINAL_STATE_MAP.forEach((location, blockState) -> blockState.update(true));
        ORIGINAL_STATE_MAP.clear();
    }

    public void reset(Location location) {
        if (!ORIGINAL_STATE_MAP.containsKey(location)) return;
        ORIGINAL_STATE_MAP.get(location).update(true);
        ORIGINAL_STATE_MAP.remove(location);
    }
}
