package org.bsdevelopment.shattered.managers.list;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.managers.IManager;
import org.bukkit.NamespacedKey;

public class KeyManager implements IManager {
    public NamespacedKey BOW_KEY;
    public NamespacedKey BOW_INFO_KEY;
    public NamespacedKey ARROW_CHILD_KEY;

    public NamespacedKey READY_SIGN_KEY;
    public NamespacedKey OPTION_SIGN_KEY;

    private final Shattered PLUGIN;

    public KeyManager(Shattered plugin) {
        PLUGIN = plugin;
    }


    @Override
    public void load() {
        BOW_KEY = new NamespacedKey(PLUGIN, "bow");
        BOW_INFO_KEY = new NamespacedKey(PLUGIN, "bow_info");
        ARROW_CHILD_KEY = new NamespacedKey(PLUGIN, "arrow_child");

        READY_SIGN_KEY = new NamespacedKey(PLUGIN, "ready_sign");
        OPTION_SIGN_KEY = new NamespacedKey(PLUGIN, "option_sign");
    }

    @Override
    public void cleanup() {
        BOW_KEY = null;
        BOW_INFO_KEY = null;
        ARROW_CHILD_KEY = null;

        READY_SIGN_KEY = null;
        OPTION_SIGN_KEY = null;
    }
}
