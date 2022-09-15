package org.bsdevelopment.shattered.managers.list;

import lib.brainsynder.utils.Triple;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.api.ShatteredAddon;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonManager implements IManager {
    private final Shattered PLUGIN;
    private final List<Triple<String, File, ShatteredAddon>> ADDON_DATA;

    public AddonManager(Shattered plugin) {
        PLUGIN = plugin;
        ADDON_DATA = new ArrayList<>();
    }

    @Override
    public void load() {
        File folder = PLUGIN.getAddonsFolder();
        if (folder.listFiles() == null) return;

        for (File file : Objects.requireNonNull(folder.listFiles())) loadAddon(file);

        if (ADDON_DATA.isEmpty())
            PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.ERROR, "No addons were loaded...");

    }

    @Override
    public void cleanup() {
        ADDON_DATA.forEach(triple -> triple.right.cleanup());

        ADDON_DATA.clear();
    }

    public List<Triple<String, File, ShatteredAddon>> getAddonData() {
        return ADDON_DATA;
    }

    public void removeAddon (Triple<String, File, ShatteredAddon> target) {
        target.right.cleanup();
        if (target != null) ADDON_DATA.remove(target);
    }

    public Triple<String, File, ShatteredAddon> getAddonData (String namespace) {
        for (Triple<String, File, ShatteredAddon> triple : ADDON_DATA) {
            if (triple.left.equalsIgnoreCase(namespace)) return triple;
        }
        return null;
    }

    public void loadAddon(File file) {
        if (file.isDirectory()) return;
        if (!file.getName().endsWith(".jar")) return;

        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());

            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> e = jarFile.entries();

                while (e.hasMoreElements()) {
                    JarEntry je = e.nextElement();

                    if (je.isDirectory() || !je.getName().endsWith(".class")) continue;
                    String className = je.getName().substring(0, je.getName().length() - 6);
                    className = className.replace('/', '.');
                    Class<?> loadClass = urlClassLoader.loadClass(className);
                    if (!ShatteredAddon.class.isAssignableFrom(loadClass)) continue;
                    try {
                        ShatteredAddon addon = (ShatteredAddon) loadClass.getDeclaredConstructor().newInstance();

                        ADDON_DATA.add(new Triple<>(addon.getNamespace().namespace(), file, addon));
                        addon.initiate();
                        break;
                    }catch (NoSuchMethodException | InvocationTargetException e1) {
                        PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.ERROR, "An error occurred when trying to load the addon: "+file.getName());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.ERROR, "Failed to load addon: "+file.getName() + " - Error Message: "+e.getMessage());

            e.printStackTrace();
        }
    }
}
