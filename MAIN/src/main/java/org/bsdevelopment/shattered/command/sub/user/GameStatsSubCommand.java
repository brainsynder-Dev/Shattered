package org.bsdevelopment.shattered.command.sub.user;

import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import lib.brainsynder.nms.Tellraw;
import lib.brainsynder.utils.Colorize;
import net.md_5.bungee.api.ChatColor;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.AdditionalUsage;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.command.CommandSender;

@ICommand(
        name = "gamestats",
        description = "Displays the stats for the current game"
)
@AdditionalUsage(
        name = "previous",
        description = "Displays the stats for the previous game"
)
@Permission(permission = "gamestats", additionalPermissions = "previous")
public class GameStatsSubCommand extends ShatteredSub {
    public GameStatsSubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length == 0) {
            outputStats(sender, Management.GAME_STATS_MANAGER.getCurrentStats());
            return;
        }

        if (args[0].equalsIgnoreCase("previous") && sender.hasPermission(getPermission("previous"))) {
            outputStats(sender, Management.GAME_STATS_MANAGER.getPreviousStats());
            return;
        }

        outputStats(sender, Management.GAME_STATS_MANAGER.getCurrentStats());
    }

    private void outputStats (CommandSender sender, StorageTagList list) {
        getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Game Stats: ");
        list.getList().forEach(base -> {
            StorageTagCompound compound = (StorageTagCompound) base;

            Tellraw.fromLegacy(MessageType.SHATTERED_DARK_BLUE+compound.getString("name"))
                    .tooltip(Colorize.translateBungeeHex(ChatColor.GRAY + compound.getString("description")))
                    .then(": ").color(ChatColor.of(MessageType.SHATTERED_BLUE.toString().replace("&", "")))
                    .then(compound.getInteger("value")).color(ChatColor.of(MessageType.SHATTERED_GRAY.toString().replace("&", "")))
                    .send(sender);
        });
    }
}
