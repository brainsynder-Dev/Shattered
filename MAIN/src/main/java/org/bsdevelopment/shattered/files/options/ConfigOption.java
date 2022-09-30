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

    public final ConfigEntry<Integer> COUNTDOWN_TIME = createOption("countdown-starting-number", 40,
            """
                    This is the number the countdown timer will start from when preparing to start the game.
                    
                    Default: {default}""");

    public final ConfigEntry<Integer> SPAWN_THRESHOLD = createOption("spawnpoint.block-threshold", 3,
            """
                    How many blocks must be around the player in order for the location to be valid
                    
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
