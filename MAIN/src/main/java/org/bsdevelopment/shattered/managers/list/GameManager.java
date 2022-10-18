package org.bsdevelopment.shattered.managers.list;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.api.ShatteredAddon;
import org.bsdevelopment.shattered.bow.list.StarterBow;
import org.bsdevelopment.shattered.events.core.GamemodeRegisterEvent;
import org.bsdevelopment.shattered.game.GameModeData;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.game.modes.list.FFAGameMode;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.GameCountdownTask;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.RandomCollection;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.*;

public class GameManager implements IManager {
    private final Shattered PLUGIN;
    private final RandomCollection<ShatteredGameMode> RANDOM_GAMEMODES;
    private final Map<String, LinkedList<ShatteredGameMode>> GAMEMODES_MAP;
    private final PluginManager PLUGIN_MANAGER;
    private final List<ShatteredPlayer> PLAYERS;

    private GameState state = GameState.WAITING;
    private ShatteredGameMode currentGamemode = null;
    private GameCountdownTask gameCountdownTask = null;

    public GameManager(Shattered plugin) {
        PLUGIN = plugin;

        RANDOM_GAMEMODES = new RandomCollection<>();
        GAMEMODES_MAP = new HashMap<>();
        PLUGIN_MANAGER = Bukkit.getPluginManager();
        PLAYERS = new ArrayList<>();
    }

    @Override
    public void load() {
        currentGamemode = null;
        registerGamemode(PLUGIN, new FFAGameMode(Shattered.INSTANCE));

        Management.GAME_OPTIONS_MANAGER.GAMEMODES.setDefaultValue(getGameMode(FFAGameMode.class));
    }

    @Override
    public void cleanup() {
        currentGamemode = null;

        GAMEMODES_MAP.values().forEach(gamemodes -> gamemodes.forEach(ShatteredGameMode::cleanup));
        GAMEMODES_MAP.clear();

        RANDOM_GAMEMODES.clear();

        PLAYERS.clear();
    }

    public GameState getState() {
        return state;
    }

    public List<ShatteredPlayer> getCurrentPlayers() {
        return PLAYERS;
    }

    public ShatteredGameMode getCurrentGamemode() {
        return currentGamemode;
    }

    public void joinGame (ShatteredPlayer shatteredPlayer, Reason reason) {
        // Checking if the player is already playing in the game.
        if (shatteredPlayer.isPlaying() || (PLAYERS.contains(shatteredPlayer))) return;
        // It adds the player to the list of players.
        PLAYERS.add(shatteredPlayer);
        shatteredPlayer.setState(ShatteredPlayer.PlayerState.LOBBY);

        if (reason == Reason.COMMAND) {
            // TODO: Store player data
            Management.LOBBY_MANAGER.joinLobby(shatteredPlayer);
        }
    }

    public void leaveGame (ShatteredPlayer player, Reason reason) {
        if (currentGamemode != null) currentGamemode.disqualifyPlayer(player);

        // It sets the player to not playing.
        player.setPlaying(false);
        // It removes the scoreboard from the player.
        player.removeBoard();
        // It removes the player from the list of players.
        PLAYERS.remove(player);

        if (reason == Reason.COMMAND) {
            // TODO: Re-store player data
            Management.LOBBY_MANAGER.leaveLobby(player);
            return;
        }
        Management.LOBBY_MANAGER.joinLobby(player);
    }

    public void setState(GameState state) {
        if (this.state == state) return;
        this.state = state;

        switch (state) {
            case WAITING: break;
            case COUNTDOWN:
                if (currentGamemode == null) currentGamemode = Management.GAME_OPTIONS_MANAGER.GAMEMODES.getValue(); // RANDOM_GAMEMODES.next();

                gameCountdownTask = new GameCountdownTask(PLUGIN, currentGamemode);

                Management.LOBBY_MANAGER.getReadyCube1().getCubePlayers().forEach(shatteredPlayer -> {
                    shatteredPlayer.setState(ShatteredPlayer.PlayerState.IN_GAME);
                    shatteredPlayer.setPlaying(true);
                    if (!PLAYERS.contains(shatteredPlayer)) PLAYERS.add(shatteredPlayer);
                });

                Management.LOBBY_MANAGER.getReadyCube2().getCubePlayers().forEach(shatteredPlayer -> {
                    shatteredPlayer.setState(ShatteredPlayer.PlayerState.IN_GAME);
                    shatteredPlayer.setPlaying(true);
                    if (!PLAYERS.contains(shatteredPlayer)) PLAYERS.add(shatteredPlayer);
                });

                if (PLUGIN.getSchematics().getCurrentRegion() == null) {
                    PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "No Map generated... generating...");
                    String mapTarget = Management.GAME_OPTIONS_MANAGER.MAP_SELECTION.getValue();

                    File mapSchematic = PLUGIN.getSchematics().getArenaMap().getOrDefault(mapTarget, null);
                    if (mapSchematic == null) mapSchematic = PLUGIN.getSchematics().getRandomMap();

                    PLUGIN.getSchematics().pasteSchematic(mapSchematic, () -> {
                        PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Generated map... starting countdown");
                        gameCountdownTask.runTaskTimer(PLUGIN, 0, 20);
                    });
                }else{
                    PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Map already there, starting countdown");
                    gameCountdownTask.runTaskTimer(PLUGIN, 0, 20);
                }
                break;
            case IN_GAME:
                gameCountdownTask = null;

                PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "GameMode.start()");
                currentGamemode.start();

                Management.GAME_STATS_MANAGER.resetStats();

                PLAYERS.forEach(shatteredPlayer -> {
                    shatteredPlayer.setPlaying(true);
                    shatteredPlayer.setState(ShatteredPlayer.PlayerState.IN_GAME);
                    shatteredPlayer.fetchPlayer(player -> {
                        player.getInventory().clear();

                        player.setGameMode(GameMode.ADVENTURE);
                        player.getInventory().setItem(17, new ItemStack(Material.ARROW));
                        player.getInventory().addItem(Management.BOW_MANAGER.getBow(StarterBow.class).getItem());

                        currentGamemode.respawnPlayer(shatteredPlayer);
                    });
                });
                break;
            case CLEANUP: {
                if (gameCountdownTask != null) {
                    gameCountdownTask.cancel();
                    gameCountdownTask = null;
                }

                if (currentGamemode != null) {
                    currentGamemode.onEnd();

                    currentGamemode = null;
                }

                PLAYERS.forEach(shatteredPlayer -> {
                    shatteredPlayer.removeBoard();

                    Management.LOBBY_MANAGER.joinLobby(shatteredPlayer);
                });

                Management.GLASS_MANAGER.resetBlocks();
                PLUGIN.getSchematics().resetRegion(() -> {
                    Management.LOBBY_MANAGER.getReadyCube1().toggleCube(false);
                    Management.LOBBY_MANAGER.getReadyCube2().toggleCube(false);

                    setState(GameState.WAITING);
                });

                break;
            }
        }
    }

    /**
     * It registers a gamemode
     *
     * @param addon The addon that is registering the gamemode.
     * @param gameMode The gamemode to register.
     */
    public void registerGamemode (ShatteredAddon addon, ShatteredGameMode gameMode) {
        registerGamemode0(addon.getNamespace().namespace(), gameMode, GamemodeRegisterEvent.Type.ADDON);
        Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Registered gamemode '"+MessageType.SHATTERED_GREEN+gameMode.getGameModeData().name()+MessageType.SHATTERED_GRAY+"' from the addon: "+MessageType.SHATTERED_GREEN+addon.getNamespace().namespace());
    }

    /**
     * Register a gamemode for a plugin.
     *
     * @param plugin The plugin that registered the gamemode.
     * @param gameMode The gamemode to register
     */
    private void registerGamemode (Plugin plugin, ShatteredGameMode gameMode) {
        registerGamemode0(plugin.getDescription().getName(), gameMode, GamemodeRegisterEvent.Type.PLUGIN);

        if (gameMode instanceof Listener listener) PLUGIN_MANAGER.registerEvents(listener, plugin);
    }

    /**
     * It adds a game mode to a list of game modes
     *
     * @param key The key to register the gamemode under.
     * @param gameMode The GameMode to register
     * @param type The type of gamemode.
     */
    private void registerGamemode0 (String key, ShatteredGameMode gameMode, GamemodeRegisterEvent.Type type) {
        LinkedList<ShatteredGameMode> list = GAMEMODES_MAP.getOrDefault(key, new LinkedList<>());
        list.addLast(gameMode);
        RANDOM_GAMEMODES.add(gameMode);
        GAMEMODES_MAP.put(key, list);

        gameMode.initiate();

        ShatteredUtilities.fireShatteredEvent(new GamemodeRegisterEvent(new GamemodeRegisterEvent.Culprit(type, key), gameMode));

        Management.GAME_OPTIONS_MANAGER.GAMEMODES.setValueList(RANDOM_GAMEMODES.values());
    }

    public ShatteredGameMode getGameMode (Class<?> gamemodeClass) {
        for (ShatteredGameMode gameMode : RANDOM_GAMEMODES.values()) {
            if (gameMode.getClass().getCanonicalName().equals(gamemodeClass.getCanonicalName())) return gameMode;
        }
        return null;
    }

    public ShatteredGameMode getGameMode (String gamemodeName) {
        for (ShatteredGameMode gameMode : RANDOM_GAMEMODES.values()) {
            GameModeData data = gameMode.getGameModeData();
            if (data.name().equals(gamemodeName)) return gameMode;
        }
        return null;
    }

    public enum Reason {
        COMMAND,
        LOGIN_OR_OUT
    }
}
