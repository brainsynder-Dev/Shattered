package org.bsdevelopment.shattered.option;

import lib.brainsynder.nbt.StorageTagCompound;

public class StatsOption extends Option<Integer> {
    private final String description;

    private String key = null;

    public StatsOption(String displayName, String description) {
        super(displayName, 0, 0);
        this.description = description;
    }

    public StatsOption(String key, String displayName, String description) {
        super(displayName, 0, 0);
        this.description = description;
        this.key = key;
    }

    public String getKey() {
        if (key == null) return getStorageName();
        return key;
    }

    public void increase () {
        setValue(getValue()+1);
    }

    public String getDescription() {
        return description;
    }

    public StorageTagCompound toCompound () {
        StorageTagCompound compound = new StorageTagCompound();
        compound.setString("key", getKey());
        compound.setString("name", getName());
        compound.setString("description", getDescription());
        compound.setInteger("value", getValue());
        return compound;
    }

    @Override
    public String toString() {
        return toCompound().toString();
    }
}
