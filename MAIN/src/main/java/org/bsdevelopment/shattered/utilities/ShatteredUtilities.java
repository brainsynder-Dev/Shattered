package org.bsdevelopment.shattered.utilities;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import lib.brainsynder.math.MathUtils;
import lib.brainsynder.utils.BlockLocation;
import lib.brainsynder.utils.Cuboid;
import lib.brainsynder.utils.DirectionUtils;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.events.ShatteredCancelEvent;
import org.bsdevelopment.shattered.events.ShatteredEvent;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.version.VersionMatcher;
import org.bsdevelopment.shattered.version.VersionWrapper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ShatteredUtilities {
    private static final VersionWrapper WRAPPER;
    private static final PluginManager PLUGIN_MANAGER;

    static {
        PLUGIN_MANAGER = Bukkit.getPluginManager();
        WRAPPER = new VersionMatcher(Shattered.INSTANCE).match();
    }

    /**
     * It highlights the block at the given location, with the color, and then removes it after a given
     * amount of time
     *
     * @param color    The color of the block.
     * @param location The location of the block you want to highlight
     * @param lifeTime The time in ticks that the block will be highlighted for.
     * @param plugin   The plugin that is calling the method.
     */
    public static void highlightBlock(ChatColor color, Location location, int lifeTime, Player... viewer) {
        if (viewer == null) throw new NullPointerException("Missing viewers to send the highlighted blocks to");
        WRAPPER.highlightBlock(color, location, lifeTime, viewer);
    }

    public static List<Block> search(Cuboid cuboid, List<Material> list) {
        List<Block> blocks = new ArrayList<>();
        cuboid.getBlocks().forEach(block -> {
            if (list.contains(block.getType())) blocks.add(block);
        });
        return blocks;
    }

    /**
     * Call the ShatteredEvent event.
     *
     * @param event The event to fire.
     */
    public static <E extends ShatteredEvent> E fireShatteredEvent(E event) {
        PLUGIN_MANAGER.callEvent(event);
        return event;
    }

    /**
     * Call the ShatteredCancelEvent event and return whether or not it was cancelled.
     *
     * @param event The event to fire.
     * @return The event is being returned.
     */
    public static boolean fireShatteredCancelEvent(ShatteredCancelEvent event) {
        PLUGIN_MANAGER.callEvent(event);
        return event.isCancelled();
    }

    /**
     * This function returns a BlockLocation object that has the same X and Z coordinates as the given Location object, but
     * has the Y coordinate of the upper bound of the given Cuboid object. (Essentially Infinite Y height)
     *
     * @param cuboid   The cuboid that you want to get the infinite Y of.
     * @param location The location of the player
     * @return A BlockLocation object.
     */
    public static BlockLocation getInfiniteY(Cuboid cuboid, Location location) {
        return new BlockLocation(location.getWorld(), location.getBlockX(), cuboid.getUpperY(), location.getBlockZ());
    }

    /**
     * Checks if the given entity is valid/alive
     *
     * @param entity The entity to check.
     * @return A boolean value.
     */
    public static boolean isValid(Entity entity) {
        if (entity == null) return false;
        if (!entity.isValid()) return false;
        if (entity.isDead()) return false;

        return (!(entity instanceof Player player)) || (player.isOnline());
    }

    /**
     * Spawns an arrow with randomized speed and spread.
     *
     * @param info     The BowInfo object that contains the information about the bow.
     * @param location The location to spawn the arrow at.
     * @param vector   The direction the arrow will be shot in.
     * @return An Arrow object.
     */
    public static Arrow spawnArrowRandomized(BowInfo info, Location location, Vector vector) {
        float speed = MathUtils.random(0.5f, 1f);
        float spread = MathUtils.random(1f, 20f);

        return spawnArrow(info, location, vector, speed, spread);
    }

    /**
     * Spawns an arrow with the given bow info, location, and vector
     *
     * @param info     The BowInfo object that contains all the information about the bow.
     * @param location The location to spawn the arrow at.
     * @param vector   The direction the arrow will be shot in.
     * @return An Arrow object
     */
    public static Arrow spawnArrow(BowInfo info, Location location, Vector vector) {
        return spawnArrow(info, location, vector, 0.6F);
    }

    /**
     * Spawns an arrow with the given bow info, location, vector, speed, and spread
     *
     * @param info     The BowInfo object that contains the information about the bow.
     * @param location The location to spawn the arrow at.
     * @param vector   The direction the arrow will be shot in.
     * @param speed    The speed of the arrow.
     * @return An Arrow
     */
    public static Arrow spawnArrow(BowInfo info, Location location, Vector vector, float speed) {
        return spawnArrow(info, location, vector, speed, 12F);
    }

    /**
     * Spawns an arrow with the given BowInfo, location, vector, speed, and spread
     *
     * @param info     The BowInfo object that contains the information about the bow.
     * @param location The location to spawn the arrow at.
     * @param vector   The direction the arrow will be shot in.
     * @param speed    The speed of the arrow.
     * @param spread   The spread of the arrow.
     * @return An Arrow object.
     */
    public static Arrow spawnArrow(BowInfo info, Location location, Vector vector, float speed, float spread) {
        Arrow arrow = Objects.requireNonNull(location.getWorld()).spawnArrow(location, vector, speed, spread);
        arrow.setShooter(info.getShooter());
        arrow.getPersistentDataContainer().set(Management.KEY_MANAGER.ARROW_CHILD_KEY, BowInfoPersistentData.INSTANCE, info);
        return arrow;
    }

    /**
     * It returns a list of blocks in a radius around a location
     *
     * @param location The location to start the radius from.
     * @param radius   The radius of the sphere.
     * @param hollow   If true, only the outer layer of blocks will be returned.
     * @return A list of blocks in a radius
     */
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

    /**
     * It gets the WorldEdit selection of the player and returns it as a Cuboid
     *
     * @param player The player who's region you want to get.
     * @return A Cuboid object
     */
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


    /**
     * It takes a vector and a spread value, and returns a new vector with the same direction but with a random spread
     *
     * @param vector The vector to spread
     * @param spread The maximum amount of spread.
     * @return A new vector with a random spread.
     */
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

    /**
     * It returns the cardinal direction that the entity is facing
     * Eg: NORTH, EAST, SOUTH, WEST
     *
     * @param entity The entity you want to get the direction of.
     * @return The direction the entity is facing.
     */
    public static DirectionUtils.Direction getCardinalDirection(Entity entity) {
        double rotation = (entity.getLocation().getYaw() - 90.0F) % 360.0F;
        if (rotation < 0.0D) rotation += 360.0D;

        if ((0.0D <= rotation) && (rotation < 45.0D)) return DirectionUtils.Direction.WEST;
        if ((45.0D <= rotation) && (rotation < 135.0D)) return DirectionUtils.Direction.NORTH;
        if ((135.0D <= rotation) && (rotation < 225.0D)) return DirectionUtils.Direction.EAST;
        if ((225.0D <= rotation) && (rotation < 315.0D)) return DirectionUtils.Direction.SOUTH;
        if ((315.0D <= rotation) && (rotation < 360.0D)) return DirectionUtils.Direction.WEST;
        return null;
    }


    public static void handleBowItem(ShatteredBow bow, Location location) {
        Item item = location.getWorld().dropItem(location, bow.getItem());
        // Setting the velocity of the item to 0.
        item.setVelocity(new Vector(0, 0, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                // Checking if the item is valid. If it is not valid, it cancels the task.
                if (!isValid(item)) {
                    cancel();
                    return;
                }

                // Checking if the game is in the IN_GAME state. If it is not, it will remove the item and cancel the task.
                if (Management.GAME_MANAGER.getState() != GameState.IN_GAME) {
                    item.remove();
                    cancel();
                    return;
                }

                // Spawning a particle at the location of the item.
                location.getWorld().spawnParticle(Particle.SPELL_INSTANT, item.getLocation(), 10, 0.5, 0.5, 0.5);
            }
        }.runTaskTimer(Shattered.INSTANCE, 0, 4);
    }


    /**
     * "Given a time interval in milliseconds, return a string representation of the interval in the format HH:MM:SS."
     *
     * The function is pretty straightforward. It uses the TimeUnit class to convert the interval to hours, minutes, and
     * seconds. It then uses String.format to return a string representation of the interval in the format HH:MM:SS
     *
     * @param interval The time in milliseconds to format.
     * @return A string in the format of HH:MM:SS
     */
    public static String formatInterval(long interval) {
        long hours = TimeUnit.MILLISECONDS.toHours(interval);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(interval) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(interval) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
