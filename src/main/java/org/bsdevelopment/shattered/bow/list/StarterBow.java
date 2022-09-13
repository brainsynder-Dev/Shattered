package org.bsdevelopment.shattered.bow.list;

import lib.brainsynder.utils.Colorize;
import org.bsdevelopment.shattered.bow.ShatteredBow;
import org.bsdevelopment.shattered.bow.annotations.BowData;
import org.bsdevelopment.shattered.bow.data.BowType;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

@BowData(name = "Bow", description = "", type = BowType.OFFENSIVE)
public class StarterBow extends ShatteredBow {

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.BOW, 1);
        item.addEnchantment(Enchantment.ARROW_INFINITE, 1);

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(Colorize.translateBungeeHex(MessageType.SHATTERED_BLUE + "Bow"));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        meta.getPersistentDataContainer().set(Management.KEY_MANAGER.BOW_KEY, PersistentDataType.STRING, getClass().getCanonicalName());

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
