package org.bsdevelopment.shattered.files.options;

import org.bsdevelopment.shattered.Shattered;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigEntry<T> {
    private final T defaultValue;
    private T value;
    private final String path;
    private final String description;

    private final List<T> LIMITS;
    private final List<String> PAST_PATHS;

    ConfigEntry(String path, T value, String description) {
        this.path = path;
        this.defaultValue = this.value = value;
        this.description = description;

        LIMITS = new ArrayList<>();
        PAST_PATHS = new ArrayList<>();
    }

    ConfigEntry(String path, T value) {
        this (path, value, null);
    }

    public List<T> getLimits() {
        return LIMITS;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    ConfigEntry<T> setPastPaths (String... paths) {
        PAST_PATHS.addAll(Arrays.asList(paths));
        return this;
    }

    ConfigEntry<T> setLimits (T... limits) {
        LIMITS.addAll(Arrays.asList(limits));
        return this;
    }

    public List<String> getPastPaths() {
        return PAST_PATHS;
    }

    public void setValue(T value, boolean saveConfig) {
        this.value = value;
        if (saveConfig) Shattered.INSTANCE.getConfiguration().set(path, value);
    }

    public T getValue() {
        return getValue(defaultValue);
    }

    public T getValue(T fallback) {
        if (value == null) return fallback;
        return value;
    }

    public String getExamples () {
        String className = defaultValue.getClass().getSimpleName();
        if (className.equalsIgnoreCase("boolean")) return "true or false";
        if (className.equalsIgnoreCase("double")) return "1.0";
        if (className.equalsIgnoreCase("integer")) return "1, 2, 3";
        if (className.equalsIgnoreCase("String")) return "\"im a string\"";
        return null;
    }

    // This is just used for testing the ConfigOptions
    public static void main(String[] args) {
        System.out.println("----------------------------------");
        List<String> paths = new ArrayList<>();
        ConfigOption.INSTANCE.getOptions().forEach((s, configEntry) -> {
            if (paths.contains(s)) {
                System.out.println("*** Duplicate Key: "+s);
                return;
            }
            System.out.println("Key:   "+s);
            if (!configEntry.PAST_PATHS.isEmpty()) {
                System.out.println("Past Keys:   ");
                for (Object path : configEntry.PAST_PATHS) {
                    System.out.println("  - "+path);
                }
            }
            paths.add(s);
            System.out.println("Value: "+configEntry.getValue() + " ("+configEntry.getValue().getClass().getSimpleName()+")");
            System.out.println("----------------------------------");
        });
    }
}