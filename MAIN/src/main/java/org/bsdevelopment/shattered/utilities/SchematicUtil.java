package org.bsdevelopment.shattered.utilities;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import lib.brainsynder.utils.AdvString;
import lib.brainsynder.utils.BlockLocation;
import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.events.core.MapClearEvent;
import org.bsdevelopment.shattered.events.core.MapGenerateEvent;
import org.bsdevelopment.shattered.managers.Management;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SchematicUtil {
    private final Shattered PLUGIN;
    private final Map<String, File> ARENA_MAP;
    private final RandomCollection<File> RANDOM_FILE;

    private com.sk89q.worldedit.world.World ADAPTED_WORLD;
    private Location CENTER_POINT = null;
    private Cuboid currentRegion = null;

    public SchematicUtil(Location centerPoint, Shattered plugin) {
        PLUGIN = plugin;

        ARENA_MAP = new HashMap<>();
        RANDOM_FILE = new RandomCollection<>();

        if (centerPoint == null) return;
        CENTER_POINT = centerPoint;
        ADAPTED_WORLD = BukkitAdapter.adapt(CENTER_POINT.getWorld());

        if (plugin.getDataStorage().hasKey("previous-map-region")) {
            currentRegion = new Cuboid (plugin.getDataStorage().getCompoundTag("previous-map-region"));

            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Clearing previously saved map region...");
                    resetRegion(() -> plugin.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Map region has been cleared"));
                }
            }.runTaskLater(plugin, 30);
        }
    }

    public void loadMapFiles () {
        ARENA_MAP.clear();
        RANDOM_FILE.clear();

        if ((PLUGIN.getSchematicsFolder() == null) || (Objects.requireNonNull(PLUGIN.getSchematicsFolder().listFiles()).length == 0))
            throw new NullPointerException("No maps were located in the maps folder");

        ARENA_MAP.put("RANDOM", null);

        for (File file : Objects.requireNonNull(PLUGIN.getSchematicsFolder().listFiles())) {
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format == null) continue;
            if (!format.isFormat(file)) continue;

            String name = file.getName();
            if (!name.contains(".")) continue;
            if (name.endsWith(".disabled")) continue;
            name = AdvString.beforeLast(".", name);

            ARENA_MAP.put(name, file);
            RANDOM_FILE.add(file);
        }
    }

    public File getRandomMap () {
        if (RANDOM_FILE.isEmpty()) loadMapFiles();

        return RANDOM_FILE.next();
    }

    public Map<String, File> getArenaMap() {
        return ARENA_MAP;
    }

    public Cuboid getCurrentRegion() {
        return currentRegion;
    }

    private void saveRegion () {
        PLUGIN.getDataStorage().setTag("previous-map-region", currentRegion.serialize());
        PLUGIN.getDataStorage().save();
    }

    public void setBlocks (List<Location> locations, Material material, Runnable runnable) {
        if (locations.isEmpty()) return;
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(locations.get(0).getWorld());

        CompletableFuture.runAsync(() -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).maxBlocks(-1).build()) {
                for (Location location : locations) {
                    int x = location.getBlockX();
                    int y = location.getBlockY();
                    int z = location.getBlockZ();

                    editSession.setBlock(x, y, z, Objects.requireNonNull(BlockTypes.parse(material.name())).getDefaultState());
                }
            } finally {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                }.runTask(PLUGIN);
            }
        });
    }

    public void replaceBlocks (Cuboid cuboid, Material fromMaterial, Material toMaterial, Runnable runnable) {
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(cuboid.getWorld());

        CompletableFuture.runAsync(() -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).maxBlocks(-1).build()) {
                editSession.setMask(new BlockMask (editSession.extent).add(BlockTypes.parse(fromMaterial.name())));

                editSession.setBlocks(fromCuboid(cuboid), Objects.requireNonNull(BlockTypes.parse(toMaterial.name())).getDefaultState());
            } finally {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                }.runTask(PLUGIN);
            }
        });
    }

    public void resetRegion (Runnable runnable){
        if (currentRegion == null) return;

        com.sk89q.worldedit.world.World world = ADAPTED_WORLD;
        Region current_region = fromCuboid(currentRegion);
        Cuboid regionCopy = currentRegion;

        currentRegion = null;

        Management.ARENA_MANAGER.cleanup();

        CompletableFuture.runAsync(() -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).maxBlocks(-1).build()) {
                editSession.setBlocks(current_region, Objects.requireNonNull(BlockTypes.AIR).getDefaultState());
            } finally {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ShatteredUtilities.fireShatteredEvent(new MapClearEvent(regionCopy));
                        runnable.run();
                    }
                }.runTask(PLUGIN);
            }
        });
    }

    public void pasteSchematic(File schematicFile, Runnable runnable) {
        if (currentRegion != null) {
            resetRegion(() -> pasteSchematic0(schematicFile, runnable));
            return;
        }

        pasteSchematic0(schematicFile, runnable);
    }

    private void pasteSchematic0(File schematicFile, Runnable runnable) {
        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);

        try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {

            Clipboard clipboard = reader.read();
            Region region = clipboard.getRegion();
            ClipboardHolder holder = new ClipboardHolder(clipboard);

            BlockVector3 to = BlockVector3.at(CENTER_POINT.getBlockX(), CENTER_POINT.getBlockY(), CENTER_POINT.getBlockZ());

            BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
            Vector3 realTo = to.toVector3().add(holder.getTransform().apply(clipboardOffset.toVector3()));
            Vector3 max = realTo.add(holder.getTransform().apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));
            currentRegion = fromRegion(new CuboidRegion(ADAPTED_WORLD, realTo.toBlockPoint(), max.toBlockPoint()));

            saveRegion();

            CompletableFuture.runAsync(() -> {
                try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(ADAPTED_WORLD).maxBlocks(-1).build()) {
                    Operation operation = holder.createPaste(editSession)
                            .to(to)
                            .ignoreAirBlocks(true).build();

                    Operations.completeBlindly(operation);
                } finally {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Management.ARENA_MANAGER.fromSchematicRegion(currentRegion);
                            ShatteredUtilities.fireShatteredEvent(new MapGenerateEvent(currentRegion));
                            runnable.run();
                        }
                    }.runTask(PLUGIN);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Region fromCuboid (Cuboid cuboid) {
        BlockVector3 min = BlockVector3.at(
                cuboid.getCorner1().getX(),
                cuboid.getCorner1().getY(),
                cuboid.getCorner1().getZ()
        );
        BlockVector3 max = BlockVector3.at(
                cuboid.getCorner2().getX(),
                cuboid.getCorner2().getY(),
                cuboid.getCorner2().getZ()
        );

        return new CuboidRegion(ADAPTED_WORLD, min, max);
    }

    private Cuboid fromRegion (Region region) {
        if (region == null) return null;

        BlockLocation min = new BlockLocation(CENTER_POINT.getWorld(),
                region.getMinimumPoint().getBlockX(),
                region.getMinimumPoint().getBlockY(),
                region.getMinimumPoint().getBlockZ());
        BlockLocation max = new BlockLocation(CENTER_POINT.getWorld(),
                region.getMaximumPoint().getBlockX(),
                region.getMaximumPoint().getBlockY(),
                region.getMaximumPoint().getBlockZ());
        return new Cuboid(min, max);
    }

    private boolean doRegionsMatch (Cuboid cuboid1, Cuboid cuboid2) {
        return (cuboid1.getLowerX() == cuboid2.getLowerX())
                && (cuboid1.getLowerY() == cuboid2.getLowerY())
                && (cuboid1.getLowerZ() == cuboid2.getLowerZ())

                && (cuboid1.getUpperX() == cuboid2.getUpperX())
                && (cuboid1.getUpperY() == cuboid2.getUpperY())
                && (cuboid1.getUpperZ() == cuboid2.getUpperZ());
    }
}
