package org.bsdevelopment.shattered.command;

import com.google.common.collect.Lists;
import lib.brainsynder.commands.ParentCommand;
import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.utils.Colorize;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.command.sub.ArenaLocationSubCommand;
import org.bsdevelopment.shattered.command.sub.BowsSubCommand;
import org.bsdevelopment.shattered.command.sub.BridgeSubCommand;
import org.bsdevelopment.shattered.command.sub.TestSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommand(name = "shattered")
public class ShatteredCommand extends ParentCommand<ShatteredSub> {

    public ShatteredCommand(Shattered shattered) {
        registerSub(new ArenaLocationSubCommand(shattered));
        registerSub(new BowsSubCommand(shattered));
        registerSub(new BridgeSubCommand(shattered));
        registerSub(new TestSubCommand(shattered));
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
            sub.sendUsage(sender);
        });

        if (adminCommands.isEmpty()) return;
        sender.sendMessage(ChatColor.RESET.toString());
        sender.sendMessage(Colorize.translateBungeeHex("&r &r &#4CCFE1[] &#c8cad0----- &#5676D7&lADMIN COMMANDS&r&#c8cad0 ----- &#4CCFE1[]"));
        adminCommands.forEach(petSubCommand -> petSubCommand.sendUsage(sender));
    }
}
