package org.bsdevelopment.shattered.command.sub;

import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.utils.Triple;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.api.ShatteredAddon;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.managers.list.AddonManager;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

@ICommand(name = "reloadAddon", usage = "[addon-name]", description = "Reloads all addons, or the specified addon")
@Permission(permission = "reloadAddon", adminCommand = true)
public class AddonReloadSubCommand extends ShatteredSub {

    public AddonReloadSubCommand(Shattered shattered) {
        super(shattered);

        registerCompletion(1, (commandSender, list, s) -> {
            Management.ADDON_MANAGER.getAddonData().forEach(triple -> list.add(triple.left));
            return true;
        });
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) return;

        if (args.length == 0) {
            reloadAll(sender);
            return;
        }

        String namespace = args[0];
        AddonManager manager = Management.ADDON_MANAGER;
        Triple<String, File, ShatteredAddon> triple = manager.getAddonData(namespace);

        if (triple == null) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Unknown addon: "+MessageType.SHATTERED_GRAY+namespace);
            return;
        }

        getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Unloaded the addon: "+MessageType.SHATTERED_BLUE+triple.left);
        manager.removeAddon(triple);

        new BukkitRunnable() {
            @Override
            public void run() {
                getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Attempting to load the "+MessageType.SHATTERED_BLUE+triple.left+MessageType.SHATTERED_GRAY+" addon");
                manager.loadAddon(triple.middle);
            }
        }.runTaskLater(getShattered(), 10);
    }

    private void reloadAll (CommandSender sender) {
        getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Unloaded all addons...");
        Management.ADDON_MANAGER.cleanup();

        new BukkitRunnable() {
            @Override
            public void run() {
                Management.ADDON_MANAGER.load();
                getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Successfully loaded "+MessageType.SHATTERED_BLUE+Management.ADDON_MANAGER.getAddonData().size()+MessageType.SHATTERED_GRAY+" addon(s)");
            }
        }.runTaskLater(getShattered(), 10);
    }
}
