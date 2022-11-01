package org.bsdevelopment.shattered.files.options;

import com.google.common.collect.Lists;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum ConfigOption {
    INSTANCE;
    private final Map<String, ConfigEntry> options = new LinkedHashMap<>();

    public final ConfigEntry<Boolean> TESTING_MODE = createOption("testing-mode", false,
            """
                    This will put the game in testing mode, which will do the following:
                    - Shortens the countdown timer down to just 10 seconds
                    - Makes it so one player can be in the game
                    - Only way to stop the game is to run the '/shattered stop' command
                    
                    Default: {default}""");

    public final ConfigEntry<Boolean> SETUP_CHECK = createOption("check-setup-on-startup", false,
            """
                    Should the plugin check if it is setup and ready to go?
                    This can be run when the plugin starts (if enabled here) or via '/shattered checkup'
                    
                    Default: {default}""");

    public final ConfigEntry<Boolean> BUNGEE_MODE = createOption("bungee-mode", false,
            """
                    This option is mostly used if the plugin is on its own server it will do the following:
                    - Will be put in the lobby when they join the server
                    - Disables the join/leave commands
                    
                    NEEDS A RESTART TO TAKE EFFECT
                    
                    Default: {default}""");

    public final ConfigEntry<Integer> COUNTDOWN_TIME = createOption("countdown-starting-number", 40,
            """
                    This is the number the countdown timer will start from when both ready cubes are ready to play
                    
                    Default: {default}""");

    public final ConfigEntry<Integer> SPAWN_THRESHOLD = createOption("spawnpoint.block-threshold", 3,
            """
                    How many blocks must be around the player in order for the location to be valid 
                    NOTE: This is blocks around the players feet
                    
                    Default: {default}""").setLimits(1, 2, 3, 4);


    public final ConfigEntry<Boolean> MESSAGING_TYPE_TIMING = createOption("allowed-message-types.timing-message", false,
            """
                    Should timing messages be sent to the console when the plugin loads
                    
                    Default: {default}""");


    public final ConfigEntry<Boolean> MESSAGING_TYPE_DEBUG = createOption("allowed-message-types.debug-message", true,
            """
                    Should debug/developer messages be sent to the console when the plugin loads
                    
                    Default: {default}""");


    public final ConfigEntry<String> FFA_SCOREBOARD_LINE = createOption("gamemode.ffa.scoreboard", "&7{name}: &a{lives}",
            """
                    How should the line that displays the players remaining lives look
                    
                    NOTE: HEX colors can NOT be used here only normal color codes can
                    
                    Default: {default}""");
    public final ConfigEntry<String> FFA_OPTION_NAME = createOption("gamemode.ffa.option.name", "FFA Lives",
            """
                    This is the text that gets displayed on the game option sign
                    
                    Default: {default}""");
    public final ConfigEntry<String> FFA_OPTION_DESCRIPTION = createOption("gamemode.ffa.option.description", "How many lives will you have in the FFA gamemode",
            """
                    When looking at the game option sign associated with the ffa_lives
                    this is what the description will be above the players hotbar
                    
                    Default: {default}""");
    public final ConfigEntry<Integer> FFA_DEFAULT_LIVES = createOption("gamemode.ffa.option.default-lives", 4,
            """
                    How many lives should be allowed by default for the FFA gamemode
                    
                    Default: {default}""");
    public final ConfigEntry<List<Integer>> FFA_LIFE_LIST = createOption("gamemode.ffa.option.life-list", Lists.newArrayList(),
            """
                    A list of life values that could be referenced when changing the amount of lives for the game.
                    
                    NOTE: This will override the life-range section
                    
                    Default: {default}""");
    public final ConfigEntry<Integer> FFA_RANGE_START = createOption("gamemode.ffa.option.life-range.start", 0,
            """
                    The starting number for the life-range, this will be the first number in the list
                    
                    Example: [0, 1, 2, 3, 4, 5]
                             The starting number is 0
                    
                    Default: {default}""");
    public final ConfigEntry<Integer> FFA_RANGE_END = createOption("gamemode.ffa.option.life-range.end", 20,
            """
                    The ending number for the life-range.
                    This number is the final number of the list and will prevent more numbers from being greater than it.
                    
                    Example: [0, 1, 2, 3, 4, 5]
                             The ending number is 5
                    
                    Default: {default}""");
    public final ConfigEntry<Integer> FFA_RANGE_INCREMENT = createOption("gamemode.ffa.option.life-range.increment", 1,
            """
                    How many number(s) should be added to the start number till it reaches the end number
                    
                    Example: [0, 1, 2, 3, 4, 5]
                             The increment for this list is 1
                    Example: Here is another example, lets say you have a start of 0, an end of 20, and an increment of 4
                             This will be your list of possible numbers: [0, 4, 8, 12, 16, 20]
                    
                    Default: {default}""");







    private <T> ConfigEntry<T> createOption(String key, T value, String description) {
        ConfigEntry<T> option = new ConfigEntry<>(key, value, description);
        options.put(key, option);
        return option;
    }
    private <T> ConfigEntry<T> createOption(String key, T value) {
        ConfigEntry<T> option = new ConfigEntry<>(key, value);
        options.put(key, option);
        return option;
    }

    public Map<String, ConfigEntry> getOptions() {
        return options;
    }
}
