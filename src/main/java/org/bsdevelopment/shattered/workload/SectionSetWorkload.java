package org.bsdevelopment.shattered.workload;

import lib.brainsynder.reflection.Reflection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public class SectionSetWorkload implements Workload {

    private final BlockState blockData;
    private final Location location;
    private final boolean physics;

    public SectionSetWorkload(BlockData blockData, Location location, boolean physics) {
        this.blockData = getBlockState(blockData);
        this.location = location;
        this.physics = physics;
    }

    @Override
    public boolean compute() {
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        ServerLevel level = Reflection.getWorldHandle(location.getWorld());
        LevelChunk chunk = level.getChunk(x, z);

        int j = x & 15;
        int k = y & 15;
        int l = z & 15;

        LevelChunkSection[] sections = chunk.getSections();
        LevelChunkSection section = getSection(chunk, sections, y);

        if (level.capturedTileEntities.containsKey(blockPos)) level.removeBlockEntity(blockPos);

        section.setBlockState(j, k, l, blockData, physics);
        level.setBlockAndUpdate(blockPos, blockData);
        return true;
    }

    public LevelChunkSection getSection(LevelChunk chunk, LevelChunkSection[] sections, int y) {
        return sections[getSectionIndex(chunk, y)];
    }

    public int getSectionIndex(LevelChunk chunk, int y) {
        LevelHeightAccessor levelHeightAccessor = chunk.getHeightAccessorForGeneration();
        try {
            return  levelHeightAccessor.getSectionIndex(y);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

}
