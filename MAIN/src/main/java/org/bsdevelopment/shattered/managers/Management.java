package org.bsdevelopment.shattered.managers;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.managers.list.*;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.Bukkit;

import java.util.LinkedList;

public class Management {
    private static final LinkedList<IManager> MANAGER_LIST;

    public static KeyManager KEY_MANAGER;
    public static ArenaManager ARENA_MANAGER;
    public static BowManager BOW_MANAGER;
    public static BridgeManager BRIDGE_MANAGER;
    public static GameManager GAME_MANAGER;
    public static GameStatsManager GAME_STATS_MANAGER;
    public static GlassManager GLASS_MANAGER;
    public static PlayerManager PLAYER_MANAGER;
    public static AddonManager ADDON_MANAGER;

    static {
        MANAGER_LIST = new LinkedList<>();
    }

    public static void initiate (Shattered plugin) {
        register(KEY_MANAGER = new KeyManager(plugin));
        register(ARENA_MANAGER = new ArenaManager());
        register(BOW_MANAGER = new BowManager());
        register(BRIDGE_MANAGER = new BridgeManager(plugin));
        register(GAME_MANAGER = new GameManager(plugin));
        register(GAME_STATS_MANAGER = new GameStatsManager(plugin));
        register(GLASS_MANAGER = new GlassManager(plugin));
        register(PLAYER_MANAGER = new PlayerManager(plugin));

        register(ADDON_MANAGER = new AddonManager(plugin));
    }

    private static void register (IManager manager) {
        MANAGER_LIST.addLast(manager);
        long start = System.currentTimeMillis();
        manager.load();
        Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.TIMING, "  + The "+manager.getClass().getSimpleName()+" took "+(System.currentTimeMillis() - start)+"ms to load.");
    }

    public static void cleanup () {
        MANAGER_LIST.forEach(IManager::cleanup);

        MANAGER_LIST.clear();
    }

    public static LinkedList<IManager> getManagers() {
        return MANAGER_LIST;
    }
}
