package org.bsdevelopment.shattered.command;

import com.google.common.collect.Lists;
import lib.brainsynder.commands.ParentCommand;
import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.utils.Colorize;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.command.sub.admin.*;
import org.bsdevelopment.shattered.command.sub.user.GameStatsSubCommand;
import org.bsdevelopment.shattered.command.sub.user.JoinSubCommand;
import org.bsdevelopment.shattered.command.sub.user.LeaveSubCommand;
import org.bsdevelopment.shattered.files.options.ConfigOption;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ICommand(name = "shattered")
public class ShatteredCommand extends ParentCommand<ShatteredSub> {

    public ShatteredCommand(Shattered shattered) {
        // User Commands
        registerSub(new GameStatsSubCommand(shattered));

        if (!ConfigOption.INSTANCE.BUNGEE_MODE.getValue()) {
            registerSub(new JoinSubCommand(shattered));
            registerSub(new LeaveSubCommand(shattered));
        }

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

//        registerSub(new StartSubCommand(gameManager));
//        registerSub(new EndSubCommand(gameManager));
//        registerSub(new CreateSubCommand(gameManager));
//        registerSub(new AddBowSubCommand(gameManager));
//        registerSub(new BowSubCommand(gameManager));
//        registerSub(new AddArenaFloorSubCommand(gameManager));
//        registerSub(new AddLobbyFloorSubCommand(gameManager));
//        registerSub(new LobbySubCommand(gameManager));
//        registerSub(new DisableSubCommand(gameManager));
//        registerSub(new ResetSubCommand(gameManager));
//
//        registerSub(new SpectatorSubCommand(gameManager));
//        //registerSub(new SetTeamSubCommand(gameManager));
//        registerSub(new ListSubCommand(gameManager));
//        //registerSub(new LeaveSubCommand(gameManager));
//        registerSub(new StatsSubCommand(gameManager));
//        registerSub(new ArenaRegionSubCommand (gameManager));
//        registerSub(new TestSubCommand (gameManager));
    }

    @Override
    public void run(CommandSender sender) {
        AtomicBoolean sentMainHeader = new AtomicBoolean(false);
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

            if (!sentMainHeader.get()) {
                sender.sendMessage(ChatColor.RESET.toString());
                sender.sendMessage(Colorize.translateBungeeHex("&r &r &#4CCFE1[] &#c8cad0----- &#5676D7&lMAIN COMMANDS&r&#c8cad0 ----- &#4CCFE1[]"));
                sentMainHeader.set(true);
            }
            sub.sendUsage(sender);
        });

        if (adminCommands.isEmpty()) return;
        sender.sendMessage(ChatColor.RESET.toString());
        sender.sendMessage(Colorize.translateBungeeHex("&r &r &#4CCFE1[] &#c8cad0----- &#5676D7&lADMIN COMMANDS&r&#c8cad0 ----- &#4CCFE1[]"));
        adminCommands.forEach(petSubCommand -> petSubCommand.sendUsage(sender));
    }
}
