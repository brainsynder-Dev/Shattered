package org.bsdevelopment.shattered.command.sub;

import com.google.common.collect.Lists;
import lib.brainsynder.commands.annotations.ICommand;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.AdditionalUsage;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.utilities.Cooldown;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

@ICommand(name = "test")
@AdditionalUsage(name = "mapgen", usage = "<schematic-file-name>", description = "Generates the selected map schematic as the arena location")
@AdditionalUsage(name = "reset", description = "Resets the map based on the previous region")
@Permission(permission = "test", adminCommand = true, additionalPermissions = {"mapgen", "reset"})
public class TestSubCommand extends ShatteredSub {
    private final Cooldown COOLDOWN;


    public TestSubCommand(Shattered shattered) {
        super(shattered);
        COOLDOWN = new Cooldown(5);

        registerCompletion(1, Lists.newArrayList("mapgen", "reset"));
        registerCompletion(2, (commandSender, list, s) -> {
            if (s.equalsIgnoreCase("mapgen")) {
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
        String mapsCooldownKey = getClass().getSimpleName()+" - maps";

        if (!(sender instanceof Player player)) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Must be a player to run this command");
            return;
        }

        if (args.length == 0) {
            getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Invalid command usage");
            sendUsage(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("mapgen") && sender.hasPermission(getPermission("mapgen"))) {
            if (args.length == 1) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Missing map file name");
                return;
            }

            File schematicFile = new File(getShattered().getSchematicsFolder(), args[1]);

            if (!schematicFile.exists()) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Unable to find file named '" + args[1] + "'");
                return;
            }

            if (COOLDOWN.hasCooldown(mapsCooldownKey, secondsLeft -> {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is a cooldown on this command");
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Time Left: " + MessageType.SHATTERED_GRAY +secondsLeft+"s");
            })) return;

            COOLDOWN.activateCooldown(mapsCooldownKey);


            long start = System.currentTimeMillis();

            getShattered().getSchematics().pasteSchematic(schematicFile, () -> {
                long end = (System.currentTimeMillis() - start);

                getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Finished pasting: " + MessageType.SHATTERED_BLUE + args[1]);
                getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Total Time (start -> finish): " + MessageType.SHATTERED_BLUE + formatTime(end));
            });
            return;
        }

        if (args[0].equalsIgnoreCase("reset") && sender.hasPermission(getPermission("reset"))) {
            if (COOLDOWN.hasCooldown(mapsCooldownKey, secondsLeft -> {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "There is a cooldown on this command");
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Time Left: " + MessageType.SHATTERED_GRAY +secondsLeft+"s");
            })) return;

            COOLDOWN.activateCooldown(mapsCooldownKey);

            getShattered().getSchematics().resetRegion(() -> {
                getShattered().sendPrefixedMessage(sender, MessageType.MESSAGE, "Map has been reset.");
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
