package org.bsdevelopment.shattered.workload;

import lib.brainsynder.reflection.Reflection;
import org.bukkit.block.data.BlockData;

public interface Workload {
    boolean compute();

    default <T> T getBlockState(BlockData blockData) {
        return Reflection.invokeMethod(Reflection.getMethod(Reflection.getCBCClass("block.data.CraftBlockData"), "getState"), blockData);
    }
}