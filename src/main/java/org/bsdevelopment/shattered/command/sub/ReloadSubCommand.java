package org.bsdevelopment.shattered.command.sub;

import lib.brainsynder.commands.annotations.ICommand;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.command.CommandSender;

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
        getShattered().getConfiguration().initValues();

        getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Config values have been reloaded");
    }
}
