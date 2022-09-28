package org.bsdevelopment.shattered.version;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class VersionWrapper {
    private final Plugin PLUGIN;

    protected VersionWrapper(Plugin plugin) {
        PLUGIN = plugin;
    }

    public abstract void highlightBlock(ChatColor color, Location location, int lifeTime, Player... viewer);

    public Plugin getPlugin() {
        return PLUGIN;
    }
}
