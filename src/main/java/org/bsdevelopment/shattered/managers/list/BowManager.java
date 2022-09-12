package org.bsdevelopment.shattered.managers.list;

import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.BowData;
import org.bsdevelopment.shattered.bow.list.DrunkerBow;
import org.bsdevelopment.shattered.bow.list.RainmakerBow;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.managers.Management;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BowManager implements IManager {
    private final List<ShatteredBow> BOWS;

    public BowManager() {
        BOWS = new ArrayList<>();
    }

    @Override
    public void load() {
        registerBow(new DrunkerBow());
        registerBow(new RainmakerBow());
    }

    @Override
    public void cleanup() {
        BOWS.forEach(ShatteredBow::cleanup);

        BOWS.clear();
    }

    public void registerBow (ShatteredBow bow) {
        BOWS.add(bow);
    }

    public ShatteredBow getBow (ItemStack stack) {
        ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
        String storedName = meta.getPersistentDataContainer().getOrDefault(Management.KEY_MANAGER.BOW_KEY, PersistentDataType.STRING, "UNKNOWN");

        for (ShatteredBow bow : BOWS) {
            if (bow.getClass().getCanonicalName().equals(storedName)) return bow;
        }
        throw new NullPointerException("Could not find any bows that matched the ItemStack");
    }

    public ShatteredBow getBow (Class<?> bowClass) {
        for (ShatteredBow bow : BOWS) {
            if (bow.getClass().getCanonicalName().equals(bowClass.getCanonicalName())) return bow;
        }
        throw new NullPointerException("Could not find any bows that use the class: "+bowClass.getSimpleName());
    }

    public ShatteredBow getBow (String bowName) {
        for (ShatteredBow bow : BOWS) {
            BowData data = bow.fetchBowData();
            if (data.name().equals(bowName)) return bow;
        }
        throw new NullPointerException("Could not find any bows that use the name: "+bowName);
    }

    public List<ShatteredBow> getBows() {
        return BOWS;
    }
}
