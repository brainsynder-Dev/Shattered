package org.bsdevelopment.shattered.command.sub.user;

import lib.brainsynder.commands.annotations.ICommand;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommand(
        name = "leave",
        description = "Leaves the game you are currently in."
)
@Permission(permission = "leave")
public class LeaveSubCommand extends ShatteredSub {
    public LeaveSubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Must be a player to run this command");
            return;
        }

        ShatteredPlayer shatteredPlayer = Management.PLAYER_MANAGER.getShatteredPlayer(player);
        if (!shatteredPlayer.isPlaying()) {
            getShattered().sendPrefixedMessage(player, MessageType.ERROR, "You are not currently in a game.");
            return;
        }

        Management.GAME_MANAGER.leaveGame(shatteredPlayer);
    }
}
