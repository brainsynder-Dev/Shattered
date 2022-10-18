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
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommand(name = "bridge")
@AdditionalUsage(name = "add", description = "Adds your current WorldEdit selection as a particle bridge")
@AdditionalUsage(name = "remove", description = "Removes the targeted region from being a particle bridge")
@Permission(permission = "bridge", adminCommand = true, additionalPermissions = {"add", "remove"})
public class BridgeSubCommand extends ShatteredSub {

    public BridgeSubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) return;

        if (!(sender instanceof Player player)) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Must be a player to run this command");
            return;
        }

        if (args.length == 0) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Invalid command usage");
            sendUsage(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("add") && sender.hasPermission(getPermission("add"))) {
            Cuboid selection = ShatteredUtilities.getSelectedRegion(player);

            if (selection == null) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "You need to make a WorldEdit selection.");
                return;
            }

            Management.BRIDGE_MANAGER.addBridgeRegion(selection);
            getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Successfully added your selection as a particle bridge");
            getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Particles will be displayed on top of "+MessageType.SHATTERED_BLUE+"BARRIER"+MessageType.SHATTERED_GRAY+" blocks");
            return;
        }

        if (args[0].equalsIgnoreCase("remove") && sender.hasPermission(getPermission("remove"))) {
            Block target = player.getTargetBlockExact(10, FluidCollisionMode.NEVER);

            if (target == null) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "You must be looking at a block.");
                return;
            }

            int count = Management.BRIDGE_MANAGER.removeBridgeRegion(target.getLocation());

            if (count == 0) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There are no particle bridges in that location.");
                return;
            }

            getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Successfully removed "+MessageType.SHATTERED_BLUE+count+MessageType.SHATTERED_GRAY+" particle bridge region(s)");
            return;
        }

        sendUsage(sender);
    }

    public static String formatTime(long milliseconds) {
        long time = milliseconds / 1000L;
        long minutes = (time / 60 % 60);
        long seconds = (time % 60);

        if (seconds <= 0) return milliseconds + "ms";
        String formatted = "";
        if (minutes > 0) formatted += minutes + "m ";
        if (seconds > 0) formatted += seconds + "s ";
        return formatted;
    }
}
