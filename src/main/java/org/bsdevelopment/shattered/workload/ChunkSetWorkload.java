package org.bsdevelopment.shattered.workload;

import lib.brainsynder.reflection.Reflection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public class ChunkSetWorkload implements Workload {
    private final BlockState blockData;
    private final Location location;
    private final boolean physics;

    public ChunkSetWorkload(BlockData blockData, Location location, boolean physics) {
        this.blockData = getBlockState(blockData);
        this.location = location;
        this.physics = physics;
    }

    @Override
    public boolean compute() {
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        ServerLevel level = Reflection.getWorldHandle(location.getWorld());
        LevelChunk chunk = level.getChunkAt(blockPos);
        if (level.capturedTileEntities.containsKey(blockPos)) level.removeBlockEntity(blockPos);
        chunk.setBlockState(blockPos, blockData, physics);
        level.setBlockAndUpdate(blockPos, blockData);
        return true;
    }
}
