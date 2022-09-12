package org.bsdevelopment.shattered.managers;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.managers.list.BowManager;
import org.bsdevelopment.shattered.managers.list.BridgeManager;
import org.bsdevelopment.shattered.managers.list.GameStatsManager;
import org.bsdevelopment.shattered.managers.list.KeyManager;

import java.util.LinkedList;

public class Management {
    private static final LinkedList<IManager> MANAGER_LIST;

    public static KeyManager KEY_MANAGER;
    public static BowManager BOW_MANAGER;
    public static BridgeManager BRIDGE_MANAGER;
    public static GameStatsManager GAME_STATS_MANAGER;

    static {
        MANAGER_LIST = new LinkedList<>();
    }

    public static void initiate (Shattered plugin) {
        register(KEY_MANAGER = new KeyManager(plugin));
        register(BOW_MANAGER = new BowManager());
        register(BRIDGE_MANAGER = new BridgeManager(plugin));
        register(GAME_STATS_MANAGER = new GameStatsManager(plugin));
    }

    private static void register (IManager manager) {
        MANAGER_LIST.addLast(manager);

        manager.load();
    }

    public static void cleanup () {
        MANAGER_LIST.forEach(IManager::cleanup);

        MANAGER_LIST.clear();
    }

    public static LinkedList<IManager> getManagers() {
        return MANAGER_LIST;
    }
}
