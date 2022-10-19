package org.bsdevelopment.shattered.game.modes;

import lib.brainsynder.math.MathUtils;
import lib.brainsynder.utils.Colorize;
import lib.brainsynder.utils.Utilities;
import net.md_5.bungee.api.ChatColor;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bsdevelopment.shattered.game.GameModeData;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ShatteredGameMode {
    private final Shattered PLUGIN;

    private BukkitRunnable task;
    private int bowSpawnCountdown = 30;
    private boolean allowSpecialBows = true;

    public ShatteredGameMode(Shattered plugin) {
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
     * Disqualify a player from the game.
     *
     * @param shatteredPlayer The player to disqualify.
     */
    public abstract void disqualifyPlayer (ShatteredPlayer shatteredPlayer);

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
        allowSpecialBows = Management.GAME_OPTIONS_MANAGER.SPECIAL_BOWS.getValue();

        Management.GAME_MANAGER.getCurrentPlayers().forEach(shatteredPlayer -> {
            shatteredPlayer.getOrCreateBoard(fastBoard -> {
                fastBoard.updateTitle(Colorize.translateBungeeHex(MessageType.SHATTERED_BLUE+getGameModeData().name()));
            });
        });

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (Management.GAME_MANAGER.getState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }

                tick();

                Management.GAME_MANAGER.getCurrentPlayers().forEach(shatteredPlayer -> {
                    // It's getting the FastBoard object from the player, and if it doesn't exist, it's creating it. Then,
                    // it's updating the lines of the scoreboard.
                    shatteredPlayer.getOrCreateBoard(fastBoard -> {
                        if (!fastBoard.getTitle().contains(getGameModeData().name()))
                            fastBoard.updateTitle(Colorize.translateBungeeHex(MessageType.SHATTERED_BLUE+getGameModeData().name()));

                        fastBoard.updateLines(getScoreboardLines());
                    });

                    // It's checking if the player is in the game, and if they are, it's checking if they have an arrow in
                    // their inventory. If they don't, it's giving them an arrow.
                    if (shatteredPlayer.getState() == ShatteredPlayer.PlayerState.IN_GAME) {
                        shatteredPlayer.fetchPlayer(player -> {
                            if (!player.getInventory().contains(Material.ARROW))
                                player.getInventory().setItem(17, new ItemStack(Material.ARROW, 1));
                        });
                    }
                });

                if (!allowSpecialBows) return;

                if (areBowsFull()) return;

                if (bowSpawnCountdown != 0) {
                    bowSpawnCountdown--;
                    return;
                }

                // It's getting the minimum and maximum bow spawn time from the GameOptionsManager, and then it's setting
                // the bow spawn countdown to a random number between the minimum and maximum.
                int min = Management.GAME_OPTIONS_MANAGER.BOW_SPAWN_MIN.getValue();
                int max = Management.GAME_OPTIONS_MANAGER.BOW_SPAWN_MAX.getValue();

                if (min > max) {
                    bowSpawnCountdown = MathUtils.random(max, min);
                }else{
                    bowSpawnCountdown = MathUtils.random(min, max);
                }

                ShatteredBow bow = Management.BOW_MANAGER.getRandomBow();
                if (bow != null) {
                    // It's checking how many bow spawns there are, and if there are more than 3, it will spawn 2 bows, and
                    // if there are more than 5, it will spawn 3 bows.
                    int count = 1;
                    if (Management.ARENA_MANAGER.getBowSpawns().size() > 3) count = 2;
                    if (Management.ARENA_MANAGER.getBowSpawns().size() > 5) count = 3;


                    Location location = Management.ARENA_MANAGER.getRandomBowSpawns().next();
                    for (int i = 0; i != count; i++) {
                        int tries = 3;

                        // It's checking if there are any items nearby the location, and if there are, it will get a new
                        // location.
                        while (!location.getWorld().getNearbyEntities(location, 0.5,0.5,0.5, entity -> entity instanceof Item).isEmpty()) {
                            location = Management.ARENA_MANAGER.getRandomBowSpawns().next();
                            tries--;

                            if (tries == 0) return;
                        }

                        // It's spawning the bow item at the location.
                        ShatteredUtilities.handleBowItem(bow, location);
                    }
                }
            }
        };
        task.runTaskTimer(PLUGIN, 0, 20);
    }


    /**
     * If there are no bow spawns, return true. Otherwise, for each bow spawn, if there are no items nearby, return false.
     * Otherwise, return true.
     *
     * @return A boolean value.
     */
    private boolean areBowsFull () {
        if (Management.ARENA_MANAGER.getBowSpawns().isEmpty()) return true;
        for (Location location : Management.ARENA_MANAGER.getBowSpawns()) {
            if (location.getWorld().getNearbyEntities(location, 0.5,0.5,0.5, entity -> entity instanceof Item).isEmpty())
                return false;
        }

        return true;
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
    public abstract boolean checkForWin ();

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
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            player.setFallDistance(0);

            Location location = getSpawnLocation(shatteredPlayer).add(0.5, 0.5, 0.5);
            Location lookAt = Utilities.lookAt(location, Management.ARENA_MANAGER.getRegion().getCenter());
            player.teleport(lookAt);
            player.setHealth(20);

            if (Management.GAME_OPTIONS_MANAGER.LOW_GRAVITY.getValue()) player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3, true, false));
        });
    }

    /**
     * Get the next spawnable block in the arena.
     *
     * @param player The player that is being teleported.
     * @return A Location object.
     */
    public Location getSpawnLocation (ShatteredPlayer player) {
        Location location = Management.ARENA_MANAGER.getSpawnableBlocks().next();

        int tries = 0;

        while ((tries >= 10) && Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, 7, 6, 7).stream().anyMatch(entity -> entity instanceof Player)) {
            location = Management.ARENA_MANAGER.getSpawnableBlocks().next();
            tries++;
        }

        return location;
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

    public List<String> getScoreboardLines () {
        return new ArrayList<>();
    }

    /**
     * If the victim and killer are the same player, return false. Otherwise, return true.
     *
     * @param victim The player who is being attacked
     * @param killer The player who is attacking
     * @return A boolean value.
     */
    public boolean canDamagePlayer (ShatteredPlayer victim, ShatteredPlayer killer) {
        return !victim.getUuid().equals(killer.getUuid());
    }

    /**
     * When a player dies, increase the death count, respawn the player, and broadcast a message
     *
     * @param shatteredPlayer The ShatteredPlayer object of the player who died.
     * @param reasons The reason the player died.
     * @param respawn If the player should respawn or not.
     */
    public void onDeath (ShatteredPlayer shatteredPlayer, DeathReasons reasons, boolean respawn) {
        Management.GAME_STATS_MANAGER.DEATHS.increase();
        if (respawn) respawnPlayer(shatteredPlayer);
        if (reasons == DeathReasons.PLAYER) return;

        if (respawn) {
            broadcastMessage(getColor(shatteredPlayer) + shatteredPlayer.getName()+MessageType.SHATTERED_GRAY+" was killed by "+MessageType.SHATTERED_BLUE+reasons.name());
            checkForWin();
        }

    }

    /**
     * When a player dies by another player, broadcast a message to the server
     *
     * @param victim The player who died
     * @param killer The player who killed the victim
     * @param respawn Whether or not the player should respawn.
     */
    public void onDeathByPlayer (ShatteredPlayer victim, ShatteredPlayer killer, boolean respawn) {
        onDeath(victim, DeathReasons.PLAYER, respawn);

        broadcastMessage(getColor(victim) + victim.getName()+MessageType.SHATTERED_GRAY+" was killed by "+getColor(killer)+killer.getName());
    }

    /**
     * For each player in the game, send them a message.
     *
     * @param message The message to broadcast.
     */
    public void broadcastMessage (String message) {
        Management.GAME_MANAGER.getCurrentPlayers().forEach(shatteredPlayer0 -> {
            shatteredPlayer0.fetchPlayer(player -> {
                PLUGIN.sendPrefixedMessage(player, MessageType.NO_PREFIX, message);
            });
        });
    }

    public enum DeathReasons {
        UNKNOWN,
        FALL_DAMAGE,
        PLAYER,
        VOID
    }

    @Override
    public String toString() {
        return getGameModeData().name();
    }
}
