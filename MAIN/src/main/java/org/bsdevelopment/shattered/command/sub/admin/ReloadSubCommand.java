package org.bsdevelopment.shattered.command.sub.admin;

import lib.brainsynder.commands.annotations.ICommand;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.command.CommandSender;

import java.io.IOException;

@ICommand(
        name = "reload",
        description = "Reloads the config file values"
)
@Permission(permission = "reload", adminCommand = true)
public class ReloadSubCommand extends ShatteredSub {

    public ReloadSubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) return;
        try {
            getShattered().getConfiguration().initValues();

            Management.ARENA_MANAGER.fromSchematicRegion(getShattered().getSchematics().getCurrentRegion());

            getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Plugin has been reloaded");
        } catch (IOException e) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Unable to reload plugin, Error message: "+e.getMessage());
        }

    }
}
