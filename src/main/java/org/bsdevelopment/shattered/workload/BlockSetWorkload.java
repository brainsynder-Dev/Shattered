package org.bsdevelopment.shattered.workload;

import lib.brainsynder.reflection.Reflection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public class BlockSetWorkload implements Workload {

    private final BlockState blockData;
    private final Location location;
    private final int physics;

    public BlockSetWorkload(BlockData blockData, Location location, boolean physics) {
        this.blockData = getBlockState(blockData);
        this.location = location;
        this.physics = physics ? 3 : 2;
    }

    @Override
    public boolean compute() {
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        ServerLevel level = Reflection.getWorldHandle(location.getWorld());
        if (level.capturedTileEntities.containsKey(blockPos)) level.removeBlockEntity(blockPos);
        level.setBlock(blockPos, blockData, physics);
        return true;
    }

}
