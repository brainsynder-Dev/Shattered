package org.bsdevelopment.shattered.command.sub;

import com.google.common.collect.Lists;
import lib.brainsynder.commands.annotations.ICommand;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.AdditionalUsage;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.managers.list.ArenaManager;
import org.bsdevelopment.shattered.utilities.Cooldown;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.ShatteredUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

@ICommand(name = "map")
@AdditionalUsage(name = "generate", usage = "<schematic-file-name>", description = "Generates the selected map schematic as the arena location")
@AdditionalUsage(name = "reset", description = "Resets the map based on the previous region")
@AdditionalUsage(name = "highlight", description = "Highlights all key locations in the map")
@Permission(permission = "mapgen", adminCommand = true, additionalPermissions = {"generate", "reset", "highlight"})
public class MapSubCommand extends ShatteredSub {
    private final Cooldown COOLDOWN;
    private final Cooldown LONG_COOLDOWN;


    public MapSubCommand(Shattered shattered) {
        super(shattered);
        COOLDOWN = new Cooldown(5);
        LONG_COOLDOWN = new Cooldown(10);

        registerCompletion(1, Lists.newArrayList("generate", "reset", "highlight"));
        registerCompletion(2, (commandSender, list, s) -> {
            if (s.equalsIgnoreCase("generate")) {
                for (File file : shattered.getSchematicsFolder().listFiles()) {
                    if ((!file.getName().endsWith(".schem")) && (!file.getName().endsWith(".schematic"))) continue;
                    list.add(file.getName());
                }
                return true;
            }

            return false;
        });
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) return;
        String mapsCooldownKey = getClass().getSimpleName() + " - maps";

        if (!(sender instanceof Player player)) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Must be a player to run this command");
            return;
        }

        if (args.length == 0) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Invalid command usage");
            sendUsage(sender);
            return;
        }
        String rawArgument = args[0];

        if (rawArgument.equalsIgnoreCase("highlight") && sender.hasPermission(getPermission("highlight"))) {
            if (getShattered().getSchematics().getCurrentRegion() == null) {
                getShattered().sendPrefixedMessage(player, MessageType.ERROR, "There is no map currently generated.");
                return;
            }

            if (LONG_COOLDOWN.hasCooldown(player.getName(), secondsLeft -> {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is a cooldown on this command");
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Time Left: " + MessageType.SHATTERED_GRAY + secondsLeft + "s");
            })) return;
            LONG_COOLDOWN.activateCooldown(player.getName());

            ArenaManager arenaManager = Management.ARENA_MANAGER;

            arenaManager.getBowSpawns().forEach(location -> {
                ShatteredUtilities.highlightBlock(ChatColor.DARK_AQUA, location, 20*9, player);
            });

            arenaManager.getGreenSpawns().values().forEach(location -> {
                ShatteredUtilities.highlightBlock(ChatColor.DARK_GREEN, location, 20*9, player);
            });

            arenaManager.getPurpleSpawns().values().forEach(location -> {
                ShatteredUtilities.highlightBlock(ChatColor.DARK_PURPLE, location, 20*9, player);
            });

            return;
        }

        if (rawArgument.equalsIgnoreCase("reset") && sender.hasPermission(getPermission("reset"))) {
            if (COOLDOWN.hasCooldown(mapsCooldownKey, secondsLeft -> {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is a cooldown on this command");
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Time Left: " + MessageType.SHATTERED_GRAY + secondsLeft + "s");
            })) return;

            COOLDOWN.activateCooldown(mapsCooldownKey);
            Management.GLASS_MANAGER.resetBlocks();

            new BukkitRunnable() {
                @Override
                public void run() {
                    getShattered().getSchematics().resetRegion(() -> {
                        getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Map has been reset.");
                    });
                }
            }.runTaskLater(getShattered(), 10);
            return;
        }

        if (rawArgument.equalsIgnoreCase("generate") && sender.hasPermission(getPermission("generate"))) {
            if (args.length == 1) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Missing map argument.");
                return;
            }

            String rawName = args[1];

            File schematicFile = new File(getShattered().getSchematicsFolder(), rawName);

            if (!schematicFile.exists()) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Unable to find file named '" + rawName + "'");
                return;
            }

            if (COOLDOWN.hasCooldown(mapsCooldownKey, secondsLeft -> {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is a cooldown on this command");
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Time Left: " + MessageType.SHATTERED_GRAY + secondsLeft + "s");
            })) return;

            COOLDOWN.activateCooldown(mapsCooldownKey);


            long start = System.currentTimeMillis();

            getShattered().getSchematics().pasteSchematic(schematicFile, () -> {
                long end = (System.currentTimeMillis() - start);

                getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Finished pasting: " + MessageType.SHATTERED_BLUE + rawName);
                getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Total Time (start -> finish): " + MessageType.SHATTERED_BLUE + formatTime(end));
            });
            return;
        }

        sendUsage(sender);
    }

    public static String formatTime(long milliseconds) {
        long time = milliseconds / 1000L;
        long hours = (time / 60 / 60 % 24);
        long minutes = (time / 60 % 60);
        long seconds = (time % 60);

        if (seconds <= 0) return milliseconds + "ms";
        String formatted = "";
        if (hours > 0) formatted += hours + "h ";
        if (minutes > 0) formatted += minutes + "m ";
        if (seconds > 0) formatted += seconds + "s ";
        return formatted;
    }
}
