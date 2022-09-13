package org.bsdevelopment.shattered.managers.list;

import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.BowData;
import org.bsdevelopment.shattered.bow.list.DrunkerBow;
import org.bsdevelopment.shattered.bow.list.RainmakerBow;
import org.bsdevelopment.shattered.bow.list.ScatterBlastBow;
import org.bsdevelopment.shattered.bow.list.StarterBow;
import org.bsdevelopment.shattered.events.core.BowRegisterEvent;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.LinkedList;
import java.util.Objects;

public class BowManager implements IManager {
    private final LinkedList<ShatteredBow> BOWS;

    public BowManager() {
        BOWS = new LinkedList<>();
    }

    @Override
    public void load() {
        registerBow(new StarterBow());
        registerBow(new DrunkerBow());
        registerBow(new RainmakerBow());
        registerBow(new ScatterBlastBow());
    }

    @Override
    public void cleanup() {
        BOWS.forEach(ShatteredBow::cleanup);

        BOWS.clear();
    }

    public void registerBow (ShatteredBow bow) {
        BOWS.add(bow);

        ShatteredUtilities.fireShatteredEvent(new BowRegisterEvent(bow));
    }

    public ShatteredBow getBow (ItemStack stack) {
        ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
        String storedName = meta.getPersistentDataContainer().getOrDefault(Management.KEY_MANAGER.BOW_KEY, PersistentDataType.STRING, "UNKNOWN");

        for (ShatteredBow bow : BOWS) {
            if (bow.getClass().getCanonicalName().equals(storedName)) return bow;
        }
        return null;
    }

    public ShatteredBow getBow (Class<?> bowClass) {
        for (ShatteredBow bow : BOWS) {
            if (bow.getClass().getCanonicalName().equals(bowClass.getCanonicalName())) return bow;
        }
        return null;
    }

    public ShatteredBow getBow (String bowName) {
        for (ShatteredBow bow : BOWS) {
            BowData data = bow.fetchBowData();
            if (data.name().equals(bowName)) return bow;
        }
        return null;
    }

    public LinkedList<ShatteredBow> getBows() {
        return BOWS;
    }
}
