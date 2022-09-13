package org.bsdevelopment.shattered.utilities;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import lib.brainsynder.utils.BlockLocation;
import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.events.ShatteredCancelEvent;
import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ShatteredUtilities {
    private static final PluginManager PLUGIN_MANAGER;

    static {
        PLUGIN_MANAGER = Bukkit.getPluginManager();
    }

    public static void fireShatteredEvent (ShatteredEvent event) {
        PLUGIN_MANAGER.callEvent(event);
    }

    public static boolean fireShatteredCancelEvent (ShatteredCancelEvent event) {
        PLUGIN_MANAGER.callEvent(event);
        return event.isCancelled();
    }

    public static BlockLocation getInfiniteY (Cuboid cuboid, Location location) {
        return new BlockLocation(location.getWorld(), location.getBlockX(), cuboid.getUpperY(), location.getBlockZ());
    }

    public static boolean isValid (Entity entity) {
        if (entity == null) return false;
        if (!entity.isValid()) return false;
        if (entity.isDead()) return false;

        return (!(entity instanceof Player player)) || (player.isOnline());
    }

    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
        List<Block> blocks = new ArrayList<>();

        int bX = location.getBlockX();
        int bY = location.getBlockY();
        int bZ = location.getBlockZ();
        for (int x = bX - radius; x <= bX + radius; x++) {
            for (int y = bY - radius; y <= bY + radius; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {
                    double distance = (bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z);
                    if ((distance < radius * radius) && ((!hollow) || (distance >= (radius - 1) * (radius - 1)))) {
                        Location l = new Location(location.getWorld(), x, y, z);
                        if (l.getBlock().getType() != org.bukkit.Material.BARRIER) {
                            blocks.add(l.getBlock());
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public static Cuboid getSelectedRegion(Player player) {
        WorldEditPlugin worldEdit = (WorldEditPlugin) Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"));

        Region region = null;
        try {
            region = worldEdit.getSession(player).getSelection(new BukkitWorld(player.getWorld()));
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }
        if (region == null) return null;


        BlockLocation min = new BlockLocation(player.getWorld(),
                region.getMinimumPoint().getBlockX(),
                region.getMinimumPoint().getBlockY(),
                region.getMinimumPoint().getBlockZ());
        BlockLocation max = new BlockLocation(player.getWorld(),
                region.getMaximumPoint().getBlockX(),
                region.getMaximumPoint().getBlockY(),
                region.getMaximumPoint().getBlockZ());
        return new Cuboid(min, max);
    }



    public static Vector spread(Vector vector, float spread) {
        Random random = new Random();
        double x = random.nextDouble() * spread;
        double y = random.nextDouble() * spread;
        double z = random.nextDouble() * spread;

        if (random.nextBoolean()) {
            x = vector.getX() + x;
        } else {
            x = vector.getX() - x;
        }
        if (random.nextBoolean()) {
            y = vector.getY() + y;
        } else {
            y = vector.getY() - y;
        }
        if (random.nextBoolean()) {
            z = vector.getZ() + z;
        } else {
            z = vector.getZ() - z;
        }

        return new Vector(x, y, z);
    }

}
