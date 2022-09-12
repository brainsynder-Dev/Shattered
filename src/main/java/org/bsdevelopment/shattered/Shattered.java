package org.bsdevelopment.shattered;

import lib.brainsynder.commands.CommandRegistry;
import lib.brainsynder.utils.Colorize;
import org.bsdevelopment.shattered.command.ShatteredCommand;
import org.bsdevelopment.shattered.files.Config;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.listeners.BowArrowListener;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.SchematicUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Shattered extends JavaPlugin {
    public static Shattered INSTANCE;

    private DataStorage dataStorage;
    private Config configuration;

    private File SCHEMATICS_FOLDER;
    private SchematicUtil SCHEMATICS;

    @Override
    public void onEnable() {
        INSTANCE = this;

        dataStorage = new DataStorage(this);
        try {
            configuration = new Config(new File(getDataFolder(), "config.yml"));
            configuration.load();
            configuration.initValues();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SCHEMATICS_FOLDER = new File(getDataFolder() + File.separator + "maps-schematics");
        if (!SCHEMATICS_FOLDER.exists()) SCHEMATICS_FOLDER.mkdirs();

        SCHEMATICS = new SchematicUtil(dataStorage.getLocation("arena-location", null), this);

        Management.initiate(this);

        registerListeners();

        CommandRegistry<Shattered> registry = new CommandRegistry<> (this);
        try {
            registry.register(new ShatteredCommand(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        dataStorage.save();

        Management.cleanup();

        SCHEMATICS_FOLDER = null;
        SCHEMATICS = null;
        dataStorage = null;
    }

    private void registerListeners () {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new BowArrowListener(this), this);
    }

    public void reload () {
        SCHEMATICS = new SchematicUtil(dataStorage.getLocation("arena-location", null), this);
    }

    public File getSchematicsFolder() {
        return SCHEMATICS_FOLDER;
    }

    public SchematicUtil getSchematics() {
        return SCHEMATICS;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

    public void sendPrefixedMessage (CommandSender sender, MessageType messageType, String message) {
        sender.sendMessage(Colorize.translateBungeeHex(messageType.getPrefix()+message));
    }

    public Config getConfiguration() {
        return configuration;
    }
}
