package org.bsdevelopment.shattered.command.sub.admin;

import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.AdditionalUsage;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.option.Option;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@ICommand(
        name = "lobby",
        description = "Main command for all the lobby setup"
)
@AdditionalUsage(name = "lobbyspawn", description = "Sets the spawn point of the lobby", checkPermission = true)
@AdditionalUsage(name = "readycube1", description = "Sets the region for one of the ready cubes", checkPermission = true)
@AdditionalUsage(name = "readycube2", description = "Sets the region for one of the ready cubes", checkPermission = true)
@AdditionalUsage(name = "optionsign", usage = "<option>", description = "Sets the location of a sign that will allow modifications to that option", checkPermission = true)
@Permission(permission = "lobby", adminCommand = true, additionalPermissions = {"lobbyspawn", "readycube1", "readycube2", "optionsign"})
public class LobbySubCommand extends ShatteredSub {

    public LobbySubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public List<String> handleCompletions(List<String> completions, CommandSender sender, int index, String[] args) {
        if (!canExecute(sender)) return super.handleCompletions(completions, sender, index, args);

        if ((index == 2) && args[0].equalsIgnoreCase("optionsign")) {
            Management.GAME_OPTIONS_MANAGER.getOptions().forEach(option -> {
                completions.add(option.getCombinedName());
            });
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

        if (args[0].equalsIgnoreCase("optionsign") && sender.hasPermission(getPermission("optionsign"))) {
            if (args.length == 1) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Invalid command usage");
                sendUsage(sender);
                return;
            }

            Option option = Management.GAME_OPTIONS_MANAGER.getOptionFromName(args[1], true);
            if (option == null) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Unable to find option by the name of: "+MessageType.SHATTERED_GRAY+args[1]);
                return;
            }

            Block block = player.getTargetBlockExact(10, FluidCollisionMode.NEVER);

            if ((block == null) || (!(block.getState() instanceof Sign sign))) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Invalid block targeted, please look at a sign");
                return;
            }

            Management.GAME_OPTIONS_MANAGER.setSign(option, sign);
            getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Successfully set the sign for "+MessageType.SHATTERED_BLUE+option.getName());
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
