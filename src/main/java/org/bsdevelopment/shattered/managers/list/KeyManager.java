package org.bsdevelopment.shattered.managers.list;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.managers.IManager;
import org.bukkit.NamespacedKey;

public class KeyManager implements IManager {
    public NamespacedKey BOW_KEY;
    public NamespacedKey BOW_INFO_KEY;

    private final Shattered PLUGIN;

    public KeyManager(Shattered plugin) {
        PLUGIN = plugin;
    }


    @Override
    public void load() {
        BOW_KEY = new NamespacedKey(PLUGIN, "bow");
        BOW_INFO_KEY = new NamespacedKey(PLUGIN, "bow_info");
    }

    @Override
    public void cleanup() {
        BOW_KEY = null;
        BOW_INFO_KEY = null;
    }
}
