package org.bsdevelopment.shattered.command.sub.admin;

import lib.brainsynder.commands.annotations.ICommand;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommand(
        name = "setMapRegion",
        description = "Sets the center point of where the arenas will generate."
)
@Permission(permission = "setmapregion", adminCommand = true)
public class ArenaLocationSubCommand extends ShatteredSub {

    public ArenaLocationSubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) return;

        if (!(sender instanceof Player player)) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Must be a player to run this command");
            return;
        }

        getShattered().getDataStorage().setLocation("arena-location", player.getLocation());
        getShattered().reload();

        getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Arena location has been set.");
    }
}
