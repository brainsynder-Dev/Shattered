package org.bsdevelopment.shattered.command.sub;

import lib.brainsynder.commands.annotations.ICommand;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommand(
        name = "bows",
        description = "Gives you all the bows registered in shattered"
)
@Permission(permission = "bows", adminCommand = true)
public class BowsSubCommand extends ShatteredSub {

    public BowsSubCommand(Shattered shattered) {
        super(shattered);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) return;

        if (!(sender instanceof Player player)) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Must be a player to run this command");
            return;
        }

        Management.BOW_MANAGER.getBows().forEach(shatteredBow -> {
            player.getWorld().dropItem(player.getEyeLocation(), shatteredBow.getItem(), item -> item.setPickupDelay(0));
        });

        getShattered().sendPrefixedMessage(player, MessageType.MESSAGE, "You have been given " + MessageType.SHATTERED_BLUE + Management.BOW_MANAGER.getBows().size() + MessageType.SHATTERED_GRAY + " different bows.");
    }
}
