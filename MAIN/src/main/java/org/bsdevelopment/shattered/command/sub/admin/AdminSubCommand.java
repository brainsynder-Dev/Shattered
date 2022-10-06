package org.bsdevelopment.shattered.command.sub.admin;

import com.google.common.collect.Lists;
import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.utils.AdvString;
import lib.brainsynder.utils.Cuboid;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.AdditionalUsage;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.game.GameState;
import org.bsdevelopment.shattered.game.ShatteredPlayer;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.managers.list.ArenaManager;
import org.bsdevelopment.shattered.managers.list.GameManager;
import org.bsdevelopment.shattered.managers.list.LobbyManager;
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
import java.util.UUID;

@ICommand(name = "admin")
@AdditionalUsage(name = "startcountdown", description = "Starts the game countdown")
@AdditionalUsage(name = "forcestop", description = "Forces a game to end, no winners will be selected.")
@AdditionalUsage(name = "corners", description = "Highlights the corner of the current map region")
@AdditionalUsage(name = "readycubes", description = "Highlights the ready cube regions")
@AdditionalUsage(name = "spawnpoints", usage = "<tries>", description = "Will generate spawn points around the map")
@AdditionalUsage(name = "playerstress", usage = "<iterations>", description = "Will add x amount of fake players to the data-storage file")
@Permission(permission = "admin", adminCommand = true, additionalPermissions = {"corners", "spawnpoints", "playerstress", "startcountdown"})
public class AdminSubCommand extends ShatteredSub {
    private final Cooldown LONG_COOLDOWN;

    public AdminSubCommand(Shattered shattered) {
        super(shattered);
        LONG_COOLDOWN = new Cooldown(30);
    }

    @Override
    public List<String> handleCompletions(List<String> completions, CommandSender sender, int index, String[] args) {
        if (!canExecute(sender)) return super.handleCompletions(completions, sender, index, args);
        if (index == 1) {
            for (AdditionalUsage additionalUsage : getAdditionalUsage(getClass()))
                completions.add(additionalUsage.name());
        }

        if ((index == 2) && (args[0].equalsIgnoreCase("spawnpoints")) && (sender.hasPermission(getPermission("spawnpoints")))) {
            for (int i = 1; i != 10; i++) completions.add(String.valueOf(i));
        }
        return super.handleCompletions(completions, sender, index, args);
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
        if (args[0].equalsIgnoreCase("forcestop") && sender.hasPermission(getPermission("forcestop"))) {
            GameManager gameManager = Management.GAME_MANAGER;
            if (gameManager.getState() != GameState.IN_GAME) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is no game currently running.");
                return;
            }

            Management.GAME_MANAGER.setState(GameState.CLEANUP);
            return;
        }
        if (args[0].equalsIgnoreCase("startcountdown") && sender.hasPermission(getPermission("startcountdown"))) {
            GameManager gameManager = Management.GAME_MANAGER;
            if (gameManager.getState() != GameState.WAITING) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Unable to start the countdown");
                return;
            }

            Management.GAME_MANAGER.setState(GameState.COUNTDOWN);
            return;
        }
        if (args[0].equalsIgnoreCase("playerstress") && sender.hasPermission(getPermission("playerstress"))) {
            if (args.length == 1) {
                sendUsage(sender);
                return;
            }


            try {
                int iterations = Integer.parseInt(args[1]);

                if (iterations < 1) {
                    getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Number of iterations must not be less than 1");
                    return;
                }

                for (int i = 0; i != iterations; i++) {
                    UUID uuid = UUID.randomUUID();
                    String name = AdvString.scramble("12345ABCDEFG");
                    Management.PLAYER_MANAGER.addPlayer(new ShatteredPlayer(new StorageTagCompound().setUniqueId("uuid", uuid).setString("name", name)));
                }
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Generated " + iterations + " random players for the data-storage file");
                return;
            } catch (NumberFormatException e) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Oh no, '" + MessageType.SHATTERED_GRAY + args[1] + MessageType.SHATTERED_RED + "' does not looks like a number");
                return;
            }
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
                ShatteredUtilities.highlightBlock(ChatColor.DARK_BLUE, location, 20 * 30, player);
            });
            sideLocations.forEach(location -> {
                ShatteredUtilities.highlightBlock(ChatColor.DARK_AQUA, location, 20 * 30, player);
            });
            return;
        }


        if (args[0].equalsIgnoreCase("readycubes")) {
            if (LONG_COOLDOWN.hasCooldown(player.getName(), secondsLeft -> {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is a cooldown on this command");
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Time Left: " + MessageType.SHATTERED_GRAY + secondsLeft + "s");
            })) return;
            LONG_COOLDOWN.activateCooldown(player.getName());
            LobbyManager lobbyManager = Management.LOBBY_MANAGER;

            {
                Cuboid region = lobbyManager.getReadyCube1();
                List<Location> locations = new ArrayList<>();
                for (Block block : region.getBlocks()) {
                    int sides = 0;

                    // Checking if the block is on the edge of the region.
                    for (BlockFace face : Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
                        if (!region.contains(block.getRelative(face))) sides++;
                    }

                    if (sides >= 2) locations.add(block.getLocation());
                }

                locations.forEach(location -> {
                    ShatteredUtilities.highlightBlock(ChatColor.DARK_PURPLE, location, 20 * 30, player);
                });

                lobbyManager.getReadyDoor1().forEach(location -> {
                    ShatteredUtilities.highlightBlock(ChatColor.LIGHT_PURPLE, location, 20 * 30, player);
                });

                if (lobbyManager.getReadySign1() != null) {
                    ShatteredUtilities.highlightBlock(ChatColor.GOLD, lobbyManager.getReadySign1().getLocation(), 20 * 30, player);
                }
            }



            {
                Cuboid region = lobbyManager.getReadyCube2();
                List<Location> locations = new ArrayList<>();
                for (Block block : region.getBlocks()) {
                    int sides = 0;

                    // Checking if the block is on the edge of the region.
                    for (BlockFace face : Lists.newArrayList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
                        if (!region.contains(block.getRelative(face))) sides++;
                    }

                    if (sides >= 2) locations.add(block.getLocation());
                }

                locations.forEach(location -> {
                    ShatteredUtilities.highlightBlock(ChatColor.DARK_GREEN, location, 20 * 30, player);
                });

                lobbyManager.getReadyDoor2().forEach(location -> {
                    ShatteredUtilities.highlightBlock(ChatColor.GREEN, location, 20 * 30, player);
                });

                if (lobbyManager.getReadySign2() != null) {
                    ShatteredUtilities.highlightBlock(ChatColor.GOLD, lobbyManager.getReadySign2().getLocation(), 20 * 30, player);
                }
            }
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
                    ShatteredUtilities.highlightBlock(ChatColor.RED, location.clone(), 20 * 30, player);
                }
                return;
            } catch (NumberFormatException e) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Oh no, '" + MessageType.SHATTERED_GRAY + args[1] + MessageType.SHATTERED_RED + "' does not looks like a number");
                return;
            }
        }

        sendUsage(sender);
    }
}
