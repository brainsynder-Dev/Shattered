package org.bsdevelopment.shattered.command;

import com.google.common.collect.Lists;
import lib.brainsynder.commands.ParentCommand;
import lib.brainsynder.commands.SubCommand;
import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.utils.ListPager;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.api.ShatteredAddon;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.command.sub.admin.*;
import org.bsdevelopment.shattered.command.sub.user.GameStatsSubCommand;
import org.bsdevelopment.shattered.command.sub.user.HelpSubCommand;
import org.bsdevelopment.shattered.command.sub.user.JoinSubCommand;
import org.bsdevelopment.shattered.command.sub.user.LeaveSubCommand;
import org.bsdevelopment.shattered.files.options.ConfigOption;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@ICommand(name = "shattered")
public class ShatteredCommand extends ParentCommand<ShatteredSub> {
    private final HelpSubCommand HELP_COMMAND;
    private final Map<ShatteredAddon, List<ShatteredSub>> ADDON_SUB_COMMANDS;

    public ShatteredCommand(Shattered shattered) {
        ADDON_SUB_COMMANDS = new HashMap<>();

        // User Commands
        registerSub(new GameStatsSubCommand(shattered));
        registerSub(HELP_COMMAND= new HelpSubCommand(shattered, this));

        if (!ConfigOption.INSTANCE.BUNGEE_MODE.getValue()) {
            registerSub(new JoinSubCommand(shattered));
        }
        registerSub(new LeaveSubCommand(shattered));

        // Admin Commands
        registerSub(new AddonReloadSubCommand(shattered));
        registerSub(new ArenaLocationSubCommand(shattered));
        registerSub(new BowsSubCommand(shattered));
        registerSub(new BridgeSubCommand(shattered));
        registerSub(new CheckupSubCommand(shattered));
        registerSub(new LobbySubCommand(shattered));
        registerSub(new MapSubCommand(shattered));
        registerSub(new ReloadSubCommand(shattered));
        registerSub(new AdminSubCommand(shattered));
    }

    public void fillPager (CommandSender sender, Consumer<ListPager<ShatteredSub>> consumer) {
        ListPager<ShatteredSub> listPager = new ListPager<>(8);

        List<ShatteredSub> adminCommands = Lists.newArrayList();

        getSubCommands().forEach(sub -> {
            if (sub.getClass().isAnnotationPresent(Permission.class)) {
                Permission permission = sub.getClass().getAnnotation(Permission.class);
                if (permission.adminCommand() && sender.hasPermission(permission.permission())) {
                    adminCommands.add(sub);
                    return;
                }
                if (!permission.defaultAllow() && !sender.hasPermission(permission.permission())) return;
            }
            listPager.add(sub);
        });

        listPager.addAll(adminCommands);
        consumer.accept(listPager);
    }

    public void registerSub(ShatteredAddon addon, ShatteredSub subCommand) {
        Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "  Registered sub-command '/shattered "+MessageType.SHATTERED_GREEN+subCommand.getCommand(subCommand.getClass()).name()+MessageType.SHATTERED_GRAY+"' from the addon: "+MessageType.SHATTERED_GREEN+addon.getNamespace().namespace());
        for (SubCommand sub : getSubCommands()) {
            String name = sub.getCommand(sub.getClass()).name();
            if (name.equalsIgnoreCase(subCommand.getCommand(subCommand.getClass()).name())) {
                Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "  FAILED to register sub-command '/shattered "+MessageType.SHATTERED_GREEN+subCommand.getCommand(subCommand.getClass()).name()+MessageType.SHATTERED_GRAY+"' from the "+MessageType.SHATTERED_GREEN+addon.getNamespace().namespace()+MessageType.SHATTERED_GRAY+" addon (The command was already registered)");
                return;
            }
        }

        List<ShatteredSub> subCommands = ADDON_SUB_COMMANDS.getOrDefault(addon, new ArrayList<>());
        subCommands.add(subCommand);
        ADDON_SUB_COMMANDS.put(addon, subCommands);

        super.registerSub(subCommand);
    }

    public void unregisterCommands (ShatteredAddon addon) {
        ADDON_SUB_COMMANDS.getOrDefault(addon, new ArrayList<>()).forEach(shatteredSub -> {
            String name = shatteredSub.getCommand(shatteredSub.getClass()).name();
            Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.DEBUG, "  Unregistered sub-command '/shattered "+MessageType.SHATTERED_GREEN+name);

            getSubCommands().remove(shatteredSub);
        });

        ADDON_SUB_COMMANDS.remove(addon);
    }

    @Override
    public void run(CommandSender sender) {
        HELP_COMMAND.run(sender);
    }
}
