package org.bsdevelopment.shattered.managers.list;

import com.jeff_media.morepersistentdatatypes.DataType;
import lib.brainsynder.nbt.StorageTagCompound;
import lib.brainsynder.nbt.StorageTagList;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.DataStorage;
import org.bsdevelopment.shattered.game.modes.ShatteredGameMode;
import org.bsdevelopment.shattered.managers.IManager;
import org.bsdevelopment.shattered.managers.Management;
import org.bsdevelopment.shattered.option.*;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameOptionsManager implements IManager {
    private final Shattered PLUGIN;
    private final Map<String, List<Option>> OPTIONS;
    private final Map<Option, Location> SIGN_MAP;

    public Option<ShatteredGameMode> GAMEMODES;
    public Option<String> MAP_SELECTION;

    public Option<Boolean> DISABLE_FALL_DAMAGE;
    public Option<Boolean> GOLDEN_BOW;

    public Option<Integer> BOW_SPAWN_MIN;
    public Option<Integer> BOW_SPAWN_MAX;

    public Option<Integer> BOW_USE_MULTIPLIER;


    public GameOptionsManager(Shattered plugin) {
        PLUGIN = plugin;

        OPTIONS = new HashMap<>();
        SIGN_MAP = new HashMap<>();
    }

    public void setSign (Option option, Sign sign) {
        if (SIGN_MAP.containsKey(option)) {
            Block oldBlock = SIGN_MAP.get(option).getBlock();
            if (oldBlock.getState() instanceof Sign oldSign) {
                oldSign.setLine(0, " ");
                oldSign.setLine(1, " ");
                oldSign.setLine(2, " ");
                oldSign.setLine(3, " ");

                oldSign.getPersistentDataContainer().remove(Management.KEY_MANAGER.OPTION_SIGN_KEY);
                oldSign.update();
            }
        }

        SIGN_MAP.put(option, sign.getLocation());

        sign.setLine(0, ChatColor.GRAY + option.getName());
        sign.setLine(1, ChatColor.BOLD + "================");

        ChatColor color = ChatColor.AQUA;
        if (option instanceof BooleanOption) {
            if (((BooleanOption) option).getValue()) {
                color = ChatColor.GREEN;
            }else{
                color = ChatColor.RED;
            }
        } else if (option instanceof IntegerOption) {
            color = ChatColor.YELLOW;
        } else if (option instanceof ArenaOption){
            color = ChatColor.WHITE;
        }
        sign.setLine(2, color + "" + option.getValue());

        sign.setLine(3, ChatColor.BOLD + "================");
        sign.getPersistentDataContainer().set(Management.KEY_MANAGER.OPTION_SIGN_KEY, DataType.STRING, option.getCombinedName());

        sign.update();
    }

    public void updateSign (Option option) {
        Block block = SIGN_MAP.get(option).getBlock();
        if (!(block.getState() instanceof Sign sign)) return;

        sign.setLine(0, ChatColor.GRAY + option.getName());
        sign.setLine(1, ChatColor.BOLD + "================");

        ChatColor color = ChatColor.AQUA;
        if (option instanceof BooleanOption) {
            if (((BooleanOption) option).getValue()) {
                color = ChatColor.GREEN;
            }else{
                color = ChatColor.RED;
            }
        } else if (option instanceof IntegerOption) {
            color = ChatColor.YELLOW;
        } else if (option instanceof ArenaOption){
            color = ChatColor.WHITE;
        }
        sign.setLine(2, color + "" + option.getValue());

        sign.setLine(3, ChatColor.BOLD + "================");
        sign.update();
    }

    @Override
    public void load() {
        register(getClass(), MAP_SELECTION = new ArenaOption("Map Selector")
                .setDescription("What map should the game be played on"));

        register(getClass(), GAMEMODES = new GameModeOption("Gamemode Selection")
                .setDescription("What gamemode should be played"));

        register(getClass(), DISABLE_FALL_DAMAGE = new BooleanOption("No Fall", false)
                .setDescription("Should fall damage be disabled"));
        register(getClass(), GOLDEN_BOW = new BooleanOption("Golden Bow", true)
                .setDescription("When a player gets damaged by another player should it be an Insta-Kill"));

        register(getClass(), BOW_SPAWN_MIN = new IntegerOption("Bow Spawn Minimum", 20, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110)
                .setDescription("The fastest a bow can spawn in a game"));
        register(getClass(), BOW_SPAWN_MAX = new IntegerOption("Bow Spawn Maximum", 50, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140)
                .setDescription("The slowest a bow can spawn in a game"));

        register(getClass(), BOW_USE_MULTIPLIER = new IntegerOption("Bow Use Multiplier", 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .setDescription("Add more uses to special bows"));
    }

    public void loadSignLocations () {
        DataStorage storage = PLUGIN.getDataStorage();

        if (!storage.hasKey("option-signs")) return;

        StorageTagList list = (StorageTagList) storage.getTag("option-signs");
        list.getList().forEach(base -> {
            StorageTagCompound compound = (StorageTagCompound) base;

            String optionName = compound.getString("option", "");

            Option option = getOptionFromName(optionName, true);

            if (option == null) {
                PLUGIN.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.ERROR, " Could not load option sign for '"+optionName+"' Could it not be registered?");
                return;
            }

            SIGN_MAP.put(option, compound.getLocation("location"));
            updateSign(option);
        });
    }

    @Override
    public void cleanup() {
        OPTIONS.clear();


        StorageTagList list = new StorageTagList();
        SIGN_MAP.forEach((option, location) -> {
            StorageTagCompound compound = new StorageTagCompound();
            compound.setLocation("location", location);
            compound.setString("option", option.getCombinedName());
            list.appendTag(compound);
        });

        PLUGIN.getDataStorage().setTag("option-signs", list);
        PLUGIN.getDataStorage().save();
    }

    /**
     * If the caller's canonical name is not in the map, add it with an empty list. Then add the option to the list.
     *
     * NOTE: If the option is for a specific gamemode then the 'caller' MUST be that gamemodes class
     *
     * @param caller The class that is calling the register method.
     * @param option The option to register.
     */
    public void register (Class<?> caller, Option option) {
        String callerName = caller.getCanonicalName();

        List<Option> list = OPTIONS.getOrDefault(callerName, new ArrayList<>());
        option.setCallerName(callerName);
        list.add(option);
        OPTIONS.put(callerName, list);
    }

    /**
     * Remove the options for the given class from the options map.
     *
     * @param caller The class that is calling the method.
     */
    public void unregister (Class<?> caller) {
        OPTIONS.remove(caller.getCanonicalName());
    }

    @Nullable
    public List<Option> getOptionsForCaller (Class<?> target) {
        return OPTIONS.getOrDefault(target.getCanonicalName(), new ArrayList<>());
    }

    public List<Option> getOptions () {
        List<Option> options = new ArrayList<>();
        for (Map.Entry<String, List<Option>> optionEntry : OPTIONS.entrySet()) {
            for (Option option : optionEntry.getValue()) {
                options.add(option);
            }
        }
        return options;
    }

    public Option getOptionFromName (String search, boolean includeCallerCheck) {
        for (Map.Entry<String, List<Option>> optionEntry : OPTIONS.entrySet()) {
            for (Option option : optionEntry.getValue()) {
                if (includeCallerCheck) {
                    if (option.getCombinedName().equals(search)) return option;
                    continue;
                }
                if (option.getName().equals(search)) return option;
                if (option.getStorageName().equals(search)) return option;
            }
        }
        return null;
    }
}
