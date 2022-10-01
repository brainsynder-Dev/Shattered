package org.bsdevelopment.shattered.command.sub.user;

import lib.brainsynder.commands.annotations.ICommand;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommand(
        name = "join",
        description = "Displays the stats for the current game"
)
@Permission(permission = "join")
public class JoinSubCommand extends ShatteredSub {
    public JoinSubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Must be a player to run this command");
            return;
        }

        Management.LOBBY_MANAGER.joinLobby(Management.PLAYER_MANAGER.getShatteredPlayer(player));
    }
}
