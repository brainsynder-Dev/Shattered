package org.bsdevelopment.shattered.bow;

import lib.brainsynder.utils.AdvString;
import lib.brainsynder.utils.Colorize;
import net.md_5.bungee.api.ChatColor;
import org.bsdevelopment.shattered.bow.annotations.BowData;
import org.bsdevelopment.shattered.bow.data.BowType;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ShatteredBow {
    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract void cleanup ();

    public boolean isEnabled() {
        return enabled;
    }

    public ItemStack getItem() {
        BowData bowData = fetchBowData();

        int uses = (bowData.defaultUses());

        StringBuilder typeBuilder = new StringBuilder();
        for (BowType type : bowData.type()) {
            typeBuilder.append(MessageType.SHATTERED_GRAY).append(type.name()).append(MessageType.SHATTERED_BLUE).append(" | ");
        }

        ItemStack item = new ItemStack(Material.BOW, 1);
        item.addEnchantment(Enchantment.ARROW_INFINITE, 1);

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        ((Damageable) meta).setDamage((short) (384 - uses));

        meta.setDisplayName(Colorize.translateBungeeHex(MessageType.SHATTERED_BLUE + bowData.name()));

        List<String> lore = new ArrayList<>();
        for (String description : bowData.description()) {
            lore.add(Colorize.translateBungeeHex(MessageType.SHATTERED_GRAY + description));
        }
        lore.add(ChatColor.RED + " ");

        lore.add(Colorize.translateBungeeHex(MessageType.SHATTERED_DARK_BLUE + "Bow Type: " + AdvString.beforeLast(" | ", typeBuilder.toString())));
        lore.add(Colorize.translateBungeeHex(MessageType.SHATTERED_DARK_BLUE + "Number of Uses: " + MessageType.SHATTERED_GRAY + uses));
        lore.add(Colorize.translateBungeeHex(MessageType.SHATTERED_DARK_BLUE + "Spawn Chance: " + MessageType.SHATTERED_GRAY + bowData.spawnChance() + MessageType.SHATTERED_BLUE + "%"));

        lore.add(ChatColor.RED + " ");
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        meta.getPersistentDataContainer().set(Management.KEY_MANAGER.BOW_KEY, PersistentDataType.STRING, getClass().getCanonicalName());

        item.setItemMeta(meta);

        return item;

    }

    public BowData fetchBowData() {
        if (!getClass().isAnnotationPresent(BowData.class))
            throw new NullPointerException("Class '" + getClass().getSimpleName() + "' is missing the '@BowData' annotation");
        return getClass().getAnnotation(BowData.class);
    }

}
