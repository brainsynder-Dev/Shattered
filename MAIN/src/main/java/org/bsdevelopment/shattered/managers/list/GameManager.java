package org.bsdevelopment.shattered.managers.list;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.api.ShatteredAddon;
import org.bsdevelopment.shattered.events.core.GamemodeRegisterEvent;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.game.modes.GameMode;
import org.bsdevelopment.shattered.game.modes.list.FFAGameMode;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.RandomCollection;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class GameManager implements IManager {
    private final Shattered PLUGIN;
    private final RandomCollection<GameMode> RANDOM_GAMEMODES;
    private final Map<String, LinkedList<GameMode>> GAMEMODES_MAP;
    private final PluginManager PLUGIN_MANAGER;
    private final List<ShatteredPlayer> PLAYERS;

    private GameState state = GameState.WAITING;
    private GameMode currentGamemode = null;

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
        Management.GAME_MANAGER.registerGamemode(PLUGIN, new FFAGameMode(Shattered.INSTANCE));
    }

    @Override
    public void cleanup() {
        currentGamemode = null;

        GAMEMODES_MAP.values().forEach(gamemodes -> gamemodes.forEach(GameMode::cleanup));
        GAMEMODES_MAP.clear();
    }

    public List<ShatteredPlayer> getCurrentPlayers() {
        return PLAYERS;
    }

    public void joinGame (ShatteredPlayer player) {
        // Checking if the player is already playing in the game.
        if (player.isPlaying()) return;
        // It sets the player to playing.
        player.setPlaying(true);
        // It adds the player to the list of players.
        PLAYERS.add(player);
    }

    public void leaveGame (ShatteredPlayer player) {
        // It sets the player to not playing.
        player.setPlaying(false);
        // It removes the player from the list of players.
        PLAYERS.remove(player);
    }

    public void setState(GameState state) {
        if (this.state == state) return;
        this.state = state;

        switch (state) {
            case WAITING: break;
            case COUNTDOWN:
                if (currentGamemode == null) currentGamemode = RANDOM_GAMEMODES.next();

                break;
            case IN_GAME: break;
            case CLEANUP: break;
        }
    }

    /**
     * It registers a gamemode
     *
     * @param addon The addon that is registering the gamemode.
     * @param gameMode The gamemode to register.
     */
    public void registerGamemode (ShatteredAddon addon, GameMode gameMode) {
        registerGamemode0(addon.getNamespace().namespace(), gameMode, GamemodeRegisterEvent.Type.ADDON);
        Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "Registered gamemode '"+MessageType.SHATTERED_GREEN+gameMode.getGameModeData().name()+MessageType.SHATTERED_GRAY+"' from the addon: "+MessageType.SHATTERED_GREEN+addon.getNamespace().namespace());
    }

    /**
     * Register a gamemode for a plugin.
     *
     * @param plugin The plugin that registered the gamemode.
     * @param gameMode The gamemode to register
     */
    private void registerGamemode (Plugin plugin, GameMode gameMode) {
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
    private void registerGamemode0 (String key, GameMode gameMode, GamemodeRegisterEvent.Type type) {
        LinkedList<GameMode> list = GAMEMODES_MAP.getOrDefault(key, new LinkedList<>());
        list.addLast(gameMode);
        RANDOM_GAMEMODES.add(gameMode);

        GAMEMODES_MAP.put(key, list);

        ShatteredUtilities.fireShatteredEvent(new GamemodeRegisterEvent(new GamemodeRegisterEvent.Culprit(type, key), gameMode));
    }
}
