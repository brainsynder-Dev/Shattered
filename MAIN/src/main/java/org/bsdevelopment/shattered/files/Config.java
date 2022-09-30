package org.bsdevelopment.shattered.files;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import org.bsdevelopment.shattered.Shattered;
import org.bsdevelopment.shattered.files.options.ConfigOption;
import org.bsdevelopment.shattered.utilities.MessageType;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Config extends ConfigFile {
    public Config(@NotNull File file) throws IOException {
        super(checkFile(file));
    }

    public void load() throws IOException {
        ConfigOption.INSTANCE.getOptions().forEach((key, entry) -> {
            // Load the default values just in case
            if (entry.getDescription() == null) {
                addDefault(key, entry.getDefaultValue());
            } else {
                String description = entry.getDescription();
                if (!entry.getLimits().isEmpty()) description = description.replace("{default}", "{default}  (Allowed values: "+entry.getLimits().toString().replace("[", "").replace("]", "")+" )");
                description = description.replace("{default}", String.valueOf(entry.getDefaultValue()));

                addDefault(key, entry.getDefaultValue(), description); // Replace the {default} placeholder with what the default value is
            }

            // Moves all the old keys to the new key
            if (!entry.getPastPaths().isEmpty()) {
                entry.getPastPaths().forEach(oldKey -> {
                    move(String.valueOf(oldKey), key);
                });
            }
        });

        save();
    }


    public void initValues() throws IOException {
        reload();

        ConfigOption.INSTANCE.getOptions().forEach((key, entry) -> {
            // Fetch the configs value
            Object value = get(key);

            // Validate the value, make sure the types match to prevent booleans becoming numbers
            if (!value.getClass().getSimpleName().equals(entry.getDefaultValue().getClass().getSimpleName())) {
                Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.ERROR, "Value of '" + key + "' can not be a '" + value.getClass().getSimpleName() + "' must be a '" + entry.getDefaultValue().getClass().getSimpleName() + "'" + ((entry.getExamples() != null) ? " Example(s): " + entry.getExamples() : ""));
                value = entry.getDefaultValue();
            }

            if (!entry.getLimits().isEmpty()) {
                if (!entry.getLimits().contains(value)) {
                    Shattered.INSTANCE.sendPrefixedMessage(Bukkit.getConsoleSender(), MessageType.ERROR, "Value of '"+key+"' must be one of these values: "+entry.getLimits().toString());
                    value = entry.getDefaultValue();
                }
            }

            // Store the configured value into the Entry
            entry.setValue(value, false);
        });
    }

    static File checkFile(File file) {
        if (file.exists()) return file;

        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    private void move(String oldKey, String newKey) {
        if (!contains(oldKey)) return;

        // Relocates where the comments are located.
        if (!comments.containsKey(newKey) && comments.containsKey(oldKey)) comments.put(newKey, comments.get(oldKey));

        this.set(newKey, get(oldKey));
        this.set(oldKey, null);
    }
}
