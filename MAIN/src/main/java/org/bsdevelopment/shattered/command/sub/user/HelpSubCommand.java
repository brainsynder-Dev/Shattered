package org.bsdevelopment.shattered.command.sub.user;

import com.google.common.collect.Lists;
import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.nms.Tellraw;
import lib.brainsynder.utils.Colorize;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.ShatteredCommand;
import org.bsdevelopment.shattered.command.ShatteredSub;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ICommand(
        name = "help",
        usage = "[page]",
        description = "Displays all the commands you have access to"
)
@Permission(permission = "help")
public class HelpSubCommand extends ShatteredSub {
    private final ShatteredCommand MAIN_COMMAND;

    public HelpSubCommand(Shattered shattered, ShatteredCommand main_command) {
        super(shattered);
        MAIN_COMMAND = main_command;
    }

    @Override
    public List<String> handleCompletions(List<String> completions, CommandSender sender, int index, String[] args) {
        if (!canExecute(sender)) return super.handleCompletions(completions, sender, index, args);
        if (index == 1) {
            MAIN_COMMAND.fillPager(sender, listPager -> {
                int total = listPager.totalPages();
                if (total == 1) {
                    completions.add("1");
                }else{
                    int current = total;

                    while (current > 0) {
                        completions.add(String.valueOf(current));
                        current--;
                    }
                }
            });
        }
        return super.handleCompletions(completions, sender, index, args);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        MAIN_COMMAND.fillPager(sender, listPager -> {
            int currentPage = 1;

            if (!(sender instanceof Player)) {
                output(sender, listPager);
                return;
            }


            // &r&#4CCFE1[] &#c8cad0----- &#5676D7&lCOMMAND List 1/1&r&#c8cad0 ----- &#4CCFE1[]
            if (args.length >= 1) {
                try {
                    currentPage = Integer.parseInt(args[0]);
                }catch (NumberFormatException e) {
                    getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Sorry, " + MessageType.SHATTERED_GRAY + args[1] + MessageType.SHATTERED_RED + " is not a valid page number.");
                    return;
                }
            }

            if (currentPage > listPager.totalPages()) {
                getShattered().sendPrefixedMessage(sender, MessageType.ERROR, "Please choose a page number from "+MessageType.SHATTERED_GRAY+"1 -> "+listPager.totalPages());
                return;
            }

            output(sender, listPager.getPage(currentPage));

            Tellraw tellraw = Tellraw.fromLegacy("&r &r &r &r &#4CCFE1[] &#c8cad0----- ");

            if (currentPage != 1) {
                tellraw.then("◀  ").color("#5676D7").style(new ChatColor[]{ChatColor.BOLD}).tooltip("&7Previous Page").command("/shattered help "+(currentPage-1));
            }else{
                tellraw.then("   ").color(ChatColor.RED);
            }

            tellraw.then(" ").color(ChatColor.RED).then(currentPage+" of ").color("#4CCFE1").then(listPager.totalPages()+" ").color("#4CCFE1");

            if (currentPage != listPager.totalPages()) {
                tellraw.then("  ▶").color("#5676D7").style(new ChatColor[]{ChatColor.BOLD}).tooltip("&7Next Page").command("/shattered help "+(currentPage+1));
            }else{
                tellraw.then("   ");
            }


            tellraw.then(" ----- ").color("#c8cad0").then("[]").color("#4CCFE1").send(sender);
        });
    }

    private void output (CommandSender sender, List<ShatteredSub> list) {
        AtomicBoolean sentMainHeader = new AtomicBoolean(false);
        List<ShatteredSub> adminCommands = Lists.newArrayList();

        list.forEach(shatteredSub -> {
            if (shatteredSub.getClass().isAnnotationPresent(Permission.class)) {
                Permission permission = shatteredSub.getClass().getAnnotation(Permission.class);
                if (permission.adminCommand() && sender.hasPermission(permission.permission())) {
                    adminCommands.add(shatteredSub);
                    return;
                }
            }

            if (!sentMainHeader.get()) {
                sender.sendMessage(ChatColor.RESET.toString());
                sender.sendMessage(Colorize.translateBungeeHex("&r &r &#4CCFE1[] &#c8cad0----- &#5676D7&lMAIN COMMANDS&r&#c8cad0 ----- &#4CCFE1[]"));
                sentMainHeader.set(true);
            }
            shatteredSub.sendUsage(sender);
        });

        if (adminCommands.isEmpty()) return;
        sender.sendMessage(ChatColor.RESET.toString());
        sender.sendMessage(Colorize.translateBungeeHex("&r &r &#4CCFE1[] &#c8cad0----- &#5676D7&lADMIN COMMANDS&r&#c8cad0 ----- &#4CCFE1[]"));
        adminCommands.forEach(petSubCommand -> petSubCommand.sendUsage(sender));
    }
}
