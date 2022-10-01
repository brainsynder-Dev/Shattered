package org.bsdevelopment.shattered.managers.list;

import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.api.ShatteredAddon;
import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.BowData;
import org.bsdevelopment.shattered.bow.list.*;
import org.bsdevelopment.shattered.events.core.BowRegisterEvent;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class BowManager implements IManager {
    private final Map<String, LinkedList<ShatteredBow>> BOWS_MAP;
    private final PluginManager PLUGIN_MANAGER;

    public BowManager() {
        BOWS_MAP = new HashMap<>();
        PLUGIN_MANAGER = Bukkit.getPluginManager();
    }

    @Override
    public void load() {
        Shattered plugin = Shattered.INSTANCE;

        registerBow(plugin, new StarterBow());
        registerBow(plugin, new DrunkerBow());
        registerBow(plugin, new RainmakerBow());
        registerBow(plugin, new ScatterBlastBow());
        registerBow(plugin, new FixerBow());
        registerBow(plugin, new WallerBow());
    }

    @Override
    public void cleanup() {
        BOWS_MAP.values().forEach(bows -> bows.forEach(ShatteredBow::cleanup));

        BOWS_MAP.clear();
    }

    public void registerBow (ShatteredAddon addon, ShatteredBow bow) {
        registerBow0(addon.getNamespace().namespace(), bow);
        Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "  Registered bow '"+MessageType.SHATTERED_GREEN+bow.fetchBowData().name()+MessageType.SHATTERED_GRAY+"' from the addon: "+MessageType.SHATTERED_GREEN+addon.getNamespace().namespace());
    }

    private void registerBow (Plugin plugin, ShatteredBow bow) {
        registerBow0(plugin.getDescription().getName(), bow);

        if (bow instanceof Listener listener) PLUGIN_MANAGER.registerEvents(listener, plugin);
    }

    private void registerBow0 (String key, ShatteredBow bow) {
        LinkedList<ShatteredBow> list = BOWS_MAP.getOrDefault(key, new LinkedList<>());
        list.addLast(bow);

        BOWS_MAP.put(key, list);

        ShatteredUtilities.fireShatteredEvent(new BowRegisterEvent(bow));
    }

    public void unregisterBows (ShatteredAddon addon) {
        BOWS_MAP.remove(addon.getNamespace().namespace());
    }

    public ShatteredBow getBow (ItemStack stack) {
        ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
        String storedName = meta.getPersistentDataContainer().getOrDefault(Management.KEY_MANAGER.BOW_KEY, PersistentDataType.STRING, "UNKNOWN");

        for (ShatteredBow bow : getBows()) {
            if (bow.getClass().getCanonicalName().equals(storedName)) return bow;
        }
        return null;
    }

    public ShatteredBow getBow (Class<?> bowClass) {
        for (ShatteredBow bow : getBows()) {
            if (bow.getClass().getCanonicalName().equals(bowClass.getCanonicalName())) return bow;
        }
        return null;
    }

    public ShatteredBow getBow (String bowName) {
        for (ShatteredBow bow : getBows()) {
            BowData data = bow.fetchBowData();
            if (data.name().equals(bowName)) return bow;
        }
        return null;
    }

    public LinkedList<ShatteredBow> getBows() {
        LinkedList<ShatteredBow> bows = new LinkedList<>();
        BOWS_MAP.forEach((plugin, shatteredBows) -> shatteredBows.forEach(bows::addLast));
        return bows;
    }
}
