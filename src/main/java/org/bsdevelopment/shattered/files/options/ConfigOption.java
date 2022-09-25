package org.bsdevelopment.shattered.files.options;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ConfigOption {
    INSTANCE;
    private final Map<String, ConfigEntry> options = new LinkedHashMap<>();

    public final ConfigEntry<Integer> SPAWN_THRESHOLD = createOption("SpawnPoint.Block-Threshold", 3,
            """
                    How many blocks must be around the player in order for the location to be valid
                    
                    Default: {default}""").setLimits(1, 2, 3, 4);







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
