package org.bsdevelopment.shattered.command.sub.admin;

import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.AdditionalUsage;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@ICommand(
        name = "lobby",
        description = "Main command for all the lobby setup"
)
@AdditionalUsage(name = "lobbyspawn", description = "Sets the spawn point of the lobby")
@AdditionalUsage(name = "readycube1", description = "Sets the region for one of the ready cubes")
@AdditionalUsage(name = "readycube2", description = "Sets the region for one of the ready cubes")
@Permission(permission = "lobby", adminCommand = true, additionalPermissions = {"lobbyspawn", "readycube1", "readycube2"})
public class LobbySubCommand extends ShatteredSub {

    public LobbySubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public List<String> handleCompletions(List<String> completions, CommandSender sender, int index, String[] args) {
        if (!canExecute(sender)) return super.handleCompletions(completions, sender, index, args);
        if (index == 1) {
            for (AdditionalUsage additionalUsage : getAdditionalUsage(getClass()))
                completions.add(additionalUsage.name());
        }
        return super.handleCompletions(completions, sender, index, args);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) return;

        if (!(sender instanceof Player player)) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Must be a player to run this command");
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("lobbyspawn") && sender.hasPermission(getPermission("lobbyspawn"))) {
            Management.LOBBY_MANAGER.setLobbySpawn(player.getLocation().clone().add(0, 0.5, 0));
            getShattered().sendPrefixedMessage(player, MessageType.MESSAGE, "Successfully set the lobby spawn location.");
            return;
        }

        if (args[0].equalsIgnoreCase("readycube1") && sender.hasPermission(getPermission("readycube1"))) {
            Cuboid region = ShatteredUtilities.getSelectedRegion(player);

            if (region == null) {
                getShattered().sendPrefixedMessage(player, MessageType.ERROR, "No worldedit region selected.");
                return;
            }

            Management.LOBBY_MANAGER.setReadyCube1(region);
            getShattered().sendPrefixedMessage(player, MessageType.MESSAGE, "Successfully set the ready cube location.");
            return;
        }

        if (args[0].equalsIgnoreCase("readycube2") && sender.hasPermission(getPermission("readycube2"))) {
            Cuboid region = ShatteredUtilities.getSelectedRegion(player);

            if (region == null) {
                getShattered().sendPrefixedMessage(player, MessageType.ERROR, "No worldedit region selected.");
                return;
            }

            Management.LOBBY_MANAGER.setReadyCube2(region);
            getShattered().sendPrefixedMessage(player, MessageType.MESSAGE, "Successfully set the ready cube location.");
            return;
        }

        sendUsage(sender);
    }
}
