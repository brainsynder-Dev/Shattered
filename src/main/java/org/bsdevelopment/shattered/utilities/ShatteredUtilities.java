package org.bsdevelopment.shattered.utilities;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import lib.brainsynder.utils.BlockLocation;
import lib.brainsynder.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ShatteredUtilities {



    public static boolean isValid (Entity entity) {
        if (entity == null) return false;
        if (!entity.isValid()) return false;
        if (entity.isDead()) return false;

        return (!(entity instanceof Player player)) || (player.isOnline());
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



}
