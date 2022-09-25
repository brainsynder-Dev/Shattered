package org.bsdevelopment.shattered.command.sub;

import com.google.common.collect.Lists;
import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.AdditionalUsage;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.managers.list.ArenaManager;
import org.bsdevelopment.shattered.utilities.Cooldown;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.SchematicUtil;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@ICommand(name = "test")
@AdditionalUsage(name = "corners", description = "Highlights the corner of the current map region")
@AdditionalUsage(name = "spawnpoints", usage = "<tries>", description = "Will generate spawn points around the map")
@Permission(permission = "test", adminCommand = true, additionalPermissions = {"corners", "spawnpoints"})
public class TestSubCommand extends ShatteredSub {
    private final Cooldown LONG_COOLDOWN;

    public TestSubCommand(Shattered shattered) {
        super(shattered);
        LONG_COOLDOWN = new Cooldown(30);

        registerCompletion(1, Lists.newArrayList("corners", "spawnpoints"));
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        if (!(sender instanceof Player player)) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Must be a player to run this command");
            return;
        }

        if (args[0].equalsIgnoreCase("corners")) {
            if (LONG_COOLDOWN.hasCooldown(player.getName(), secondsLeft -> {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is a cooldown on this command");
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Time Left: " + MessageType.SHATTERED_GRAY + secondsLeft + "s");
            })) return;
            LONG_COOLDOWN.activateCooldown(player.getName());

            SchematicUtil schematicUtil = getShattered().getSchematics();

            Cuboid region = schematicUtil.getCurrentRegion();

            // Checking if the region is null.
            if (region == null) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is no map currently generated.");
                return;
            }

            List<Location> sideLocations = new ArrayList<>();
            List<Location> cornerLocations = new ArrayList<>();
            // Looping through all the blocks in the region.
            for (Block block : region.getBlocks()) {
                int sides = 0;

                // Checking if the block is on the edge of the region.
                for (BlockFace face : Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
                    if (!region.contains(block.getRelative(face))) sides++;
                }

                if (sides == 3) cornerLocations.add(block.getLocation());
                if (sides == 2) sideLocations.add(block.getLocation());
            }

            // Highlighting the blocks that are on the edge of the region.
            cornerLocations.forEach(location -> {
                ShatteredUtilities.highlightBlock(ChatColor.DARK_BLUE, location, 1000*30, "Region Corner", getShattered(), player);
            });
            sideLocations.forEach(location -> {
                ShatteredUtilities.highlightBlock(ChatColor.DARK_AQUA, location, 1000*30, "Region Edge", getShattered(), player);
            });
            return;
        }
        if (args[0].equalsIgnoreCase("spawnpoints")) {
            ArenaManager manager = Management.ARENA_MANAGER;

            if (manager.getRegion() == null) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is no map currently generated.");
                return;
            }

            if (args.length == 1) {
                sendUsage(sender);
                return;
            }

            try {
                int tries = Integer.parseInt(args[1]);

                if (tries > 200) {
                    getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Too many tries, try a number below 200");
                    return;
                }

                if (tries < 1) {
                    getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Unable to locate any points with that number of tries");
                    return;
                }

                for (int i = 0; i != tries; i++) {
                    Location location = manager.getSpawnableBlocks().next();
                    ShatteredUtilities.highlightBlock(ChatColor.RED, location.clone().add(0,1,0), 1000*30, "Random Spawn location: "+(i+1), getShattered(), player);
                }
                return;
            }catch (NumberFormatException e) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Oh no, '"+MessageType.SHATTERED_GRAY+args[1]+MessageType.SHATTERED_RED+"' does not looks like a number");
                return;
            }
        }

        sendUsage(sender);
    }
}
