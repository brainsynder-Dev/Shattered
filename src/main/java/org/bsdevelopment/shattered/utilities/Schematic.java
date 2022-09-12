package org.bsdevelopment.shattered.utilities;

import lib.brainsynder.nbt.CompressedStreamTools;
import lib.brainsynder.nbt.StorageTagCompound;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.workload.BlockSetWorkload;
import org.bsdevelopment.shattered.workload.SectionSetWorkload;
import org.bsdevelopment.shattered.workload.WorkloadRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Schematic {
    private final Shattered PLUGIN;

    private final String name;
    private short width, height, length;
    private byte[] blockDatas;
    private final Map<Integer, BlockData> palette;

    public Schematic(Shattered shattered, File schematic) {
        PLUGIN = shattered;
        name = schematic.getName().replace(".schematic", "").replace(".schem", "");
        palette = new HashMap<>();
        try {
            FileInputStream fis = new FileInputStream(schematic);
            StorageTagCompound nbt = CompressedStreamTools.readCompressed(fis);

            width = nbt.getShort("Width");
            height = nbt.getShort("Height");
            length = nbt.getShort("Length");

            blockDatas = nbt.getByteArray("BlockData");
            StorageTagCompound palette = nbt.getCompoundTag("Palette");
            palette.getKeySet().forEach(rawState -> {
                int id = palette.getInteger(rawState);
                BlockData blockData = Bukkit.createBlockData(rawState);
                this.palette.put(id, blockData);
            });

            fis.close();
        } catch (Exception ignored) {
        }
    }


    public CompletableFuture<Void> bottomToTop(Location location, boolean testing) {
        CompletableFuture<Void> workloadFinishFuture = new CompletableFuture<>();
        Map<String, List<Location>> lightsMap = new HashMap<>();
        Map<String, List<Location>> map = new HashMap<>();
        LinkedList<String> linkedList = new LinkedList<>();

        int count = 0;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    Location relative = new Location(location.getWorld(), (x + location.getX()) - (int) width / 2, y + location.getY(), (z + location.getZ()) - (int) length / 2);
                    BlockData data = palette.get((int) blockDatas[index]);
                    if (data.getMaterial() == Material.AIR) continue;
                    count++;

                    if (isLightSource(data.getMaterial())) {
                        List<Location> locationList = lightsMap.getOrDefault(data.getAsString(), new ArrayList<>());
                        locationList.add(relative);
                        lightsMap.put(data.getAsString(), locationList);
                        continue;
                    }

                    List<Location> locationList = map.getOrDefault(data.getAsString(), new ArrayList<>());
                    locationList.add(relative);
                    map.put(data.getAsString(), locationList);

                    if (!linkedList.contains(data.getAsString())) {
                        if (data.getMaterial().name().contains("QUARTZ")) {
                            linkedList.addFirst(data.getAsString());
                        } else {
                            linkedList.addLast(data.getAsString());
                        }
                    }
                }
            }
        }


        if (testing) {
            Bukkit.broadcastMessage("Block Count: "+count);
            Bukkit.broadcastMessage("Pallet Count: "+linkedList.size());
            return workloadFinishFuture;
        }

        WorkloadRunnable workloadRunnable = new WorkloadRunnable(60);
        BukkitTask workloadTask = Bukkit.getScheduler().runTaskTimer(PLUGIN, workloadRunnable, 2, 1);

        while (!linkedList.isEmpty()) {
            String rawData = linkedList.pollFirst();
            BlockData blockData = Bukkit.createBlockData(rawData);

            List<Location> locationList = map.getOrDefault(rawData, new ArrayList<>());
            locationList.forEach(location1 -> {
                SectionSetWorkload workload = new SectionSetWorkload(blockData, location1, false);
                workloadRunnable.addWorkload(workload);
            });
        }

        workloadRunnable.whenComplete(() -> {
            lightsMap.forEach((rawData, locations) -> {
                BlockData blockData = Bukkit.createBlockData(rawData);
                locations.forEach(location1 -> {
                    BlockSetWorkload workload = new BlockSetWorkload(blockData, location1, false);
                    workloadRunnable.addWorkload(workload);
                });
            });

            workloadRunnable.whenComplete(() -> {
                workloadFinishFuture.complete(null);
                workloadTask.cancel();
            });
        });

        return workloadFinishFuture;
    }

    private boolean isLightSource (Material material) {
        return switch (material) {
            case GLOWSTONE,
                    SEA_LANTERN,
                    GLOW_BERRIES,
                    TORCH,
                    REDSTONE_TORCH,
                    REDSTONE_WALL_TORCH,
                    SOUL_WALL_TORCH,
                    WALL_TORCH,
                    SOUL_TORCH,
                    SHROOMLIGHT,
                    JACK_O_LANTERN,
                    MAGMA_BLOCK,
                    LIGHT,
                    END_ROD,
                    LANTERN,
                    SOUL_LANTERN,
                    CANDLE,
                    CYAN_CANDLE,
                    BLACK_CANDLE,
                    BLUE_CANDLE,
                    BROWN_CANDLE,
                    GREEN_CANDLE,
                    LIGHT_BLUE_CANDLE,
                    GRAY_CANDLE,
                    LIGHT_GRAY_CANDLE,
                    LIME_CANDLE,
                    MAGENTA_CANDLE,
                    ORANGE_CANDLE,
                    PINK_CANDLE,
                    PURPLE_CANDLE,
                    RED_CANDLE,
                    WHITE_CANDLE,
                    YELLOW_CANDLE,
                    CANDLE_CAKE,
                    CYAN_CANDLE_CAKE,
                    BLACK_CANDLE_CAKE,
                    BLUE_CANDLE_CAKE,
                    BROWN_CANDLE_CAKE,
                    GREEN_CANDLE_CAKE,
                    LIGHT_BLUE_CANDLE_CAKE,
                    GRAY_CANDLE_CAKE,
                    LIGHT_GRAY_CANDLE_CAKE,
                    LIME_CANDLE_CAKE,
                    MAGENTA_CANDLE_CAKE,
                    ORANGE_CANDLE_CAKE,
                    PINK_CANDLE_CAKE,
                    PURPLE_CANDLE_CAKE,
                    RED_CANDLE_CAKE,
                    WHITE_CANDLE_CAKE,
                    YELLOW_CANDLE_CAKE,
                    OCHRE_FROGLIGHT,
                    PEARLESCENT_FROGLIGHT,
                    VERDANT_FROGLIGHT,
                    CAMPFIRE,
                    SOUL_CAMPFIRE,
                    REDSTONE_LAMP,
                    REDSTONE_ORE,
                    DEEPSLATE_REDSTONE_ORE,
                    BEACON -> true;
            default -> false;
        };
    }
}
