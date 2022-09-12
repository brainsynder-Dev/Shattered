package org.bsdevelopment.shattered.files.options;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ConfigOption {
    INSTANCE;
    private final Map<String, ConfigEntry> options = new LinkedHashMap<>();

    public final ConfigEntry<Boolean> SIMPLER_GUI = createOption("Simpler-Pet-GUI-Command", false,
            """
                    UGGGGGGGG This config option makes it so `/pet` opens the GUI (like `/pet gui`)
                    Requires a server restart for some reason ¯\\_(ツ)_/¯
                    
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
