package org.bsdevelopment.shattered.version;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class VersionMatcher {
    private final Plugin PLUGIN;

    public VersionMatcher(Plugin plugin) {
        PLUGIN = plugin;
    }


    public VersionWrapper match() {
        IVersion version = getVersion();
        try {
            Constructor<?> constructor = Class.forName(getClass().getPackage().getName() + ".wrappers.Wrapper_" + version.name())
                    .getConstructor(Plugin.class);

            return (VersionWrapper) constructor.newInstance(PLUGIN);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException exception) {
            throw new IllegalStateException("Failed to instantiate version wrapper for version " + version.name(), exception);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("\u001B[47m\u001B[31mShattered does not support server version \"" + version.name() + "\"", exception);
        }
    }

    public static <T extends IVersion> T getVersion () {
        String mc = between("MC: ", ")", Bukkit.getVersion());

        String[] args = mc.split("\\.");
        int[] ints = new int[] {0, 0, 0};

        if (args.length >= 1) ints[0] = Integer.parseInt(args[0]);
        if (args.length >= 2) ints[1] = Integer.parseInt(args[1]);
        if (args.length >= 3) ints[2] = Integer.parseInt(args[2]);
        Triple<Integer, Integer, Integer> triple = Triple.of(ints[0], ints[1], ints[2]);

        return (T) new IVersion() {
            @Override
            public String name() {
                if (triple.right == 0) return "v"+triple.left+"_"+triple.middle;
                return "v"+triple.left+"_"+triple.middle+"_"+triple.right;
            }

            @Override
            public String getNMS() {
                return Bukkit.getServer().getClass().getPackage().getName().substring(23);
            }

            @Override
            public Triple<Integer, Integer, Integer> getVersionParts() {
                return triple;
            }

            @Override
            public IVersion getParent() {
                return this;
            }
        };
    }

    public static String between (String first, String last, String haystack) {
        return before(last, after(first, haystack));
    }

    public static String before (String needle, String haystack) {
        return haystack.substring(0, haystack.indexOf(needle));
    }

    public static String after (String needle, String haystack) {
        return haystack.substring((haystack.indexOf(needle)+needle.length()));
    }
}
