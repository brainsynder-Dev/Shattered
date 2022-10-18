package org.bsdevelopment.shattered.managers.list;

import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.storage.RandomCollection;
import lib.brainsynder.utils.BlockLocation;
import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.managers.IManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BridgeManager implements IManager {
    private List<Cuboid> BRIDGE_REGIONS;
    private ParticleTask PARTICLE_TASK;

    private final Shattered PLUGIN;

    public BridgeManager(Shattered plugin) {
        PLUGIN = plugin;
    }


    @Override
    public void load() {
        BRIDGE_REGIONS = new ArrayList<>();

        DataStorage storage = PLUGIN.getDataStorage();
        if (!storage.hasKey("bridge-regions")) return;

        StorageTagList list = (StorageTagList) storage.getTag("bridge-regions");
        list.getList().forEach(base -> BRIDGE_REGIONS.add(new Cuboid((StorageTagCompound) base)));

        PARTICLE_TASK = new ParticleTask();
        PARTICLE_TASK.runTaskTimer(PLUGIN, 0, 9);
    }

    @Override
    public void cleanup() {
        if (PARTICLE_TASK != null) PARTICLE_TASK.cancel();

        updateDataStorage();

        // Garbage Cleanup...
        BRIDGE_REGIONS = null;
        PARTICLE_TASK = null;
    }

    public void addBridgeRegion (Cuboid cuboid) {
        BRIDGE_REGIONS.add(cuboid);

        updateDataStorage();
    }

    public int removeBridgeRegion (Location location) {
        List<Cuboid> toRemove = new ArrayList<>();

        var blockLocation = new BlockLocation(location);
        BRIDGE_REGIONS.stream().filter(cuboid -> cuboid.contains(blockLocation)).forEach(toRemove::add);
        BRIDGE_REGIONS.removeAll(toRemove);

        updateDataStorage();
        return toRemove.size();
    }

    private void updateDataStorage () {
        StorageTagList list = new StorageTagList();

        BRIDGE_REGIONS.forEach(cuboid -> list.appendTag(cuboid.serialize()));

        PLUGIN.getDataStorage().setTag("bridge-regions", list);
        PLUGIN.getDataStorage().save();
    }

    private class ParticleTask extends BukkitRunnable {
        private final RandomCollection<Boolean> RANDOM_CHANCE;

        ParticleTask() {
            RANDOM_CHANCE = new RandomCollection<>();
            RANDOM_CHANCE.add(60, true);
            RANDOM_CHANCE.add(40, false);
        }

        @Override
        public void run() {
            if (BRIDGE_REGIONS.isEmpty()) return;
            if (Bukkit.getOnlinePlayers().isEmpty()) return;

            for (Cuboid cuboid : BRIDGE_REGIONS) {
                cuboid.getBlocks().forEach(block -> {
                    if ((block.getType() == Material.BARRIER) && RANDOM_CHANCE.next()) {
                        block.getWorld().getNearbyEntities(block.getLocation(), 20,20,20,entity -> entity instanceof Player).forEach(entity -> {
                            ((Player)entity).spawnParticle(org.bukkit.Particle.ITEM_CRACK, block.getLocation().add(0.5,1.01,0.5),
                                    3, 0.35, 0, 0.35, 0, new ItemStack(Material.QUARTZ_BLOCK));
                        });
                    }
                });
            }
        }
    }
}
