package org.bsdevelopment.shattered.command;

import com.google.common.collect.Lists;
import lib.brainsynder.commands.ParentCommand;
import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.utils.ListPager;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.command.sub.admin.*;
import org.bsdevelopment.shattered.command.sub.user.GameStatsSubCommand;
import org.bsdevelopment.shattered.command.sub.user.HelpSubCommand;
import org.bsdevelopment.shattered.command.sub.user.JoinSubCommand;
import org.bsdevelopment.shattered.command.sub.user.LeaveSubCommand;
import org.bsdevelopment.shattered.files.options.ConfigOption;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Consumer;

@ICommand(name = "shattered")
public class ShatteredCommand extends ParentCommand<ShatteredSub> {
    private final HelpSubCommand HELP_COMMAND;

    public ShatteredCommand(Shattered shattered) {
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

    @Override
    public void run(CommandSender sender) {
        HELP_COMMAND.run(sender);
    }
}
