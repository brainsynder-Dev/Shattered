package org.bsdevelopment.shattered.command;

import lib.brainsynder.commands.SubCommand;
import lib.brainsynder.commands.annotations.ICommand;
import lib.brainsynder.nms.Tellraw;
import net.md_5.bungee.api.ChatColor;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.command.annotations.AdditionalUsage;
import org.bsdevelopment.shattered.command.annotations.Permission;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ShatteredSub extends SubCommand {
    private final Shattered shattered;

    public ShatteredSub(Shattered shattered) {
        this.shattered = shattered;
    }

    public Shattered getShattered() {
        return shattered;
    }



    @Override
    public void sendUsage(CommandSender sender) {
        ICommand command = this.getCommand(this.getClass());
        String usage = command.usage().trim();
        String description = command.description();


        /*
          [] /shattered test <required> [optional]

          [] /shattered test
                             mapgen <required>
                             reset

         */
        String start = "&r &r &#4CCFE1[] &#c8cad0/shattered "+command.name();

        Tellraw raw = Tellraw.fromLegacy(start);

        {
            // handles adding alias to the tooltip
            List<String> tooltip = new ArrayList<>();
            if (!description.isEmpty())
                tooltip.add(ChatColor.GRAY + description);

            if (command.alias().length != 0) {
                if (!command.alias()[0].isEmpty()) {
                    tooltip.add("&r");
                    tooltip.add("&8Alias:");
                    for (String alias : command.alias())
                        tooltip.add(ChatColor.GRAY + " - " + alias);

                }
            }
            if (!tooltip.isEmpty()) raw.tooltip(tooltip);

            parseUsage(usage, raw);
            raw.send(sender);
        }

        AdditionalUsage[] additionalUsages = getAdditionalUsage(getClass());
        if (additionalUsages.length != 0) {
            String spacer = "§r ".repeat(("  [] /shattered "+command.name()).length() / 2);
            for (AdditionalUsage additional : additionalUsages) {
                raw = Tellraw.getInstance(spacer);
                raw.then(additional.name()).color(hex2Rgb("#c8cad0"));

                // handles adding alias to the tooltip
                List<String> tooltip = new ArrayList<>();
                if (!additional.description().isEmpty())
                    tooltip.add(ChatColor.GRAY + additional.description());

                if (!tooltip.isEmpty()) raw.tooltip(tooltip);

                parseUsage(additional.usage(), raw);
                raw.send(sender);
            }
        }
    }

    private void parseUsage (String usage, Tellraw raw) {
        StringBuilder builder = new StringBuilder();
        if (!usage.isEmpty()) {
            for (char c : usage.replace(" ", "").toCharArray()) {

                if ((c == '[') || (c == '<')) raw.then(" ");

                if ((c == '<') || ((c == '>'))) {
                    if (c == '>') {
                        raw.then(builder.toString()).color(hex2Rgb("#8FA5E5")).tooltip(ChatColor.BLUE + "REQUIRED");
                        builder = new StringBuilder();
                    }

                    raw.then(c).color(hex2Rgb("#5676D7"));
                } else if ((c == '[') || (c == ']')) {
                    if (c == ']') {
                        raw.then(builder.toString()).color(hex2Rgb("#9be4ae")).tooltip(ChatColor.GREEN + "OPTIONAL");
                        builder = new StringBuilder();
                    }
                    raw.then(c).color(hex2Rgb("#4fe371"));
                } else {
                    builder.append(c);
                }
            }
        }
    }

    private org.bukkit.Color hex2Rgb(String hex) {
        if (hex.startsWith("#") && hex.length() == 7) {
            int rgb;
            try {
                rgb = Integer.parseInt(hex.substring(1), 16);
            } catch (NumberFormatException var4) {
                throw new IllegalArgumentException("Illegal hex string " + hex);
            }

            return org.bukkit.Color.fromRGB(rgb);
        } else {
            return org.bukkit.Color.RED;
        }
    }

    public AdditionalUsage[] getAdditionalUsage(Class<?> clazz) {
        return clazz.getAnnotationsByType(AdditionalUsage.class);
    }



    public boolean needsPermission() {
        return getClass().isAnnotationPresent(Permission.class);
    }

    public String getPermission() {
        if (getClass().isAnnotationPresent(Permission.class)) {
            return "shattered.commands." + getClass().getAnnotation(Permission.class).permission();
        }
        return "";
    }

    public String getPermission(String addition) {
        if (getClass().isAnnotationPresent(Permission.class)) {
            return "shattered.commands." + getClass().getAnnotation(Permission.class).permission() + "." + addition;
        }
        return "";
    }
}
