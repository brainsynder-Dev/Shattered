package org.bsdevelopment.shattered;

import lib.brainsynder.commands.CommandRegistry;
import lib.brainsynder.utils.Colorize;
import org.bsdevelopment.shattered.command.ShatteredCommand;
import org.bsdevelopment.shattered.files.Config;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.files.options.ConfigOption;
import org.bsdevelopment.shattered.listeners.BowArrowListener;
import org.bsdevelopment.shattered.listeners.JoinLeaveListener;
import org.bsdevelopment.shattered.listeners.PlayerDamageListeners;
import org.bsdevelopment.shattered.listeners.SignClickListeners;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bsdevelopment.shattered.utilities.SchematicUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Shattered extends JavaPlugin {
    public static Shattered INSTANCE;

    private DataStorage dataStorage;
    private Config configuration;

    private File SCHEMATICS_FOLDER;
    private File ADDONS_FOLDER;
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

        ADDONS_FOLDER = new File(getDataFolder() + File.separator + "addons");
        if (!ADDONS_FOLDER.exists()) ADDONS_FOLDER.mkdirs();

        SCHEMATICS = new SchematicUtil(dataStorage.getLocation("arena-location", null), this);
        SCHEMATICS.loadMapFiles();

        Management.initiate(this);

        registerListeners();

        CommandRegistry<Shattered> registry = new CommandRegistry<>(this);
        try {
            registry.register(new ShatteredCommand(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (ConfigOption.INSTANCE.SETUP_CHECK.getValue()) runSetupCheck(Bukkit.getConsoleSender());

                if (ConfigOption.INSTANCE.BUNGEE_MODE.getValue()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Management.LOBBY_MANAGER.joinLobby(Management.PLAYER_MANAGER.getShatteredPlayer(player));
                    }
                }
            }
        }.runTaskLater(this, 20);
    }

    @Override
    public void onDisable() {
        dataStorage.save();

        Management.cleanup();

        SCHEMATICS_FOLDER = null;
        SCHEMATICS = null;
        dataStorage = null;
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new BowArrowListener(this), this);
        manager.registerEvents(new JoinLeaveListener(this), this);
        manager.registerEvents(new SignClickListeners(this), this);
        manager.registerEvents(new PlayerDamageListeners(this), this);
    }

    public void reload() {
        SCHEMATICS = new SchematicUtil(dataStorage.getLocation("arena-location", null), this);
        SCHEMATICS.loadMapFiles();
    }

    public File getSchematicsFolder() {
        return SCHEMATICS_FOLDER;
    }

    public File getAddonsFolder() {
        return ADDONS_FOLDER;
    }

    public SchematicUtil getSchematics() {
        return SCHEMATICS;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

    public void sendPrefixedMessage(CommandSender sender, MessageType messageType, String message) {
        if (sender.getName().equals("CONSOLE")) {
            if ((messageType == MessageType.TIMING) && (!ConfigOption.INSTANCE.MESSAGING_TYPE_TIMING.getValue()))
                return;
            if ((messageType == MessageType.DEBUG) && (!ConfigOption.INSTANCE.MESSAGING_TYPE_DEBUG.getValue())) return;
        }
        sender.sendMessage(Colorize.translateBungeeHex(messageType.getPrefix() + message));
    }

    public Config getConfiguration() {
        return configuration;
    }


    // TODO: Add more system checks to make sure everything is setup correctly...
    public void runSetupCheck(CommandSender sender) {
        sender.sendMessage(" ");
        sendPrefixedMessage(sender, MessageType.NO_PREFIX, "Shattered Checks: ");

        // Arena/Maps
        formatCheck(sender, (Objects.requireNonNull(SCHEMATICS_FOLDER.listFiles()).length != 0),
                "Map Schematics (Have schematic files in the Maps folder)", false);
        formatCheck(sender, (dataStorage.getLocation("arena-location", null) != null),
                "Arena Location (/shattered setMapRegion)", false);

        // Lobby
        boolean lobbyCheck = (Management.LOBBY_MANAGER != null);
        formatCheck(sender, lobbyCheck, "Lobby Manager (Lobby Manager was registered)", false);
        if (lobbyCheck) {
            formatCheck(sender, (Management.LOBBY_MANAGER.getLobbySpawn() != null),
                    "Lobby Spawn Location (/shattered lobby lobbyspawn)", true);
            formatCheck(sender, (Management.LOBBY_MANAGER.getReadyCube1() != null),
                    "Lobby Ready Cube 1 (/shattered lobby readycube1)", true);
            formatCheck(sender, (Management.LOBBY_MANAGER.getReadyCube2() != null),
                    "Lobby Ready Cube 2 (/shattered lobby readycube2)", true);
        }

        sender.sendMessage(" ");
    }

    private void formatCheck (CommandSender sender, boolean check, String label, boolean spacing) {
        String good = MessageType.SHATTERED_GREEN + "☑  " + MessageType.SHATTERED_GRAY;
        String missing = MessageType.SHATTERED_RED + "☐  " + MessageType.SHATTERED_GRAY;

        sendPrefixedMessage(sender, MessageType.NO_PREFIX, (spacing ? "   " : "") + (check ? good : missing) + label);
    }
}
