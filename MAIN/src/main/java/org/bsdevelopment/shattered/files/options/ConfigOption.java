package org.bsdevelopment.shattered.files.options;

import java.util.LinkedHashMap;
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
