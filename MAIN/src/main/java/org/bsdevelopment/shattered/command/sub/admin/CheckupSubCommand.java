package org.bsdevelopment.shattered.command.sub.admin;

import lib.brainsynder.commands.annotations.ICommand;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bukkit.command.CommandSender;

@ICommand(
        name = "checkup",
        description = "Performs a checkup of the plugin to make sure everything is setup"
)
@Permission(permission = "checkup", adminCommand = true)
public class CheckupSubCommand extends ShatteredSub {

    public CheckupSubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) return;

        getShattered().runSetupCheck(sender);
    }
}
