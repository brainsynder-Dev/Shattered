package org.bsdevelopment.shattered.game.modes;

import lib.brainsynder.utils.Utilities;
import net.md_5.bungee.api.ChatColor;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.game.GameModeData;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GameMode {
    private final Shattered PLUGIN;
    private BukkitRunnable task;

    public GameMode(Shattered plugin) {
        PLUGIN = plugin;
    }

    /**
     * This function is called when the gamemode is first loaded
     */
    public abstract void initiate ();

    /**
     * This function is called when the game is closed, and all ending tasks are complete
     */
    public abstract void cleanup ();


    /**
     * If the class has the annotation, return it. If it doesn't, throw an exception
     *
     * @return The GameModeData annotation from the class.
     */
    public GameModeData getGameModeData() {
        if (getClass().isAnnotationPresent(GameModeData.class)) return getClass().getAnnotation(GameModeData.class);
        throw new NullPointerException(getClass().getSimpleName() + " is missing @GameModeData annotation for the gamemode");
    }

    /**
     * This function is called when the game starts
     */
    public void start () {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        task.runTaskTimer(PLUGIN, 0, 20);
    }

    /**
     * This function is called when the gamemode ends
     */
    public void onEnd () {
        task.cancel();
        task = null;
    }

    /**
     * This function is called every tick, and it's where you should put your game logic.
     */
    public abstract void tick ();

    /**
     * Check if a player has won the game
     */
    public abstract void checkForWin ();

    /**
     * "This function is called when a player shoots an arrow and it hits a block."
     *
     * The BowInfo object contains information about the bow that was used to shoot the arrow. The Block object contains
     * information about the block that was hit
     *
     * @param info The BowInfo object that contains all the information about the arrow.
     * @param block The block that was hit
     */
    public void onArrowHitBlock(BowInfo info, Block block) {}

    /**
     * Respawns a player at their spawn location
     *
     * @param shatteredPlayer The ShatteredPlayer object that you want to respawn.
     */
    public void respawnPlayer (ShatteredPlayer shatteredPlayer) {
        shatteredPlayer.fetchPlayer(player -> {
            Location location = getSpawnLocation(shatteredPlayer).add(0.5, 1, 0.5);
            Location lookAt = Utilities.lookAt(location, Management.ARENA_MANAGER.getRegion().getCenter());
            player.teleport(lookAt);
            player.setHealth(20);
            player.setFallDistance(0);
        });
    }

    /**
     * Get the next spawnable block in the arena.
     *
     * @param player The player that is being teleported.
     * @return A Location object.
     */
    public Location getSpawnLocation (ShatteredPlayer player) {
        return Management.ARENA_MANAGER.getSpawnableBlocks().next();
    }

    /**
     * Returns the color of what the players name will be in death messages.
     *
     * @param player The player
     * @return The color of the message.
     */
    public ChatColor getColor (ShatteredPlayer player) {
        return ChatColor.of(MessageType.SHATTERED_BLUE.toString().replace("&", ""));
    }

}
