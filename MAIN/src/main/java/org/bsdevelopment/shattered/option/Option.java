package org.bsdevelopment.shattered.option;

import java.util.Collection;
import java.util.Objects;

public class Option<T> {
    private final String name;
    private final T defaultValue;

    private T value;
    private int current = 0;
    T[] other;
    private String description;

    public Option(String name, T defaultValue, T... allValues) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.other = allValues;
        for (T t : allValues) {
            if (t == defaultValue) break;
            this.current++;
        }
    }

    public void setAllValues (Collection<T> collection) {
        this.current = 0;

        this.other = (T[]) collection.toArray();
        for (T t : collection) {
            if (t == this.defaultValue) break;
            this.current++;
        }
    }

    public Option<T> setDescription(String description) {
        this.description = description;
        return this;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getStorageName () {
        return name.replace(" ", "_").toLowerCase();
    }

    public T getValue() {
        if (value == null) return defaultValue;
        return value;
    }

    public T setValue(T value) {
        return this.value = value;
    }

    public T previous () {
        if ((current-1) == 0) {
            // Resets loop to the start
            current = (other.length-1);
            value = other[current];
            return value;
        }

        current = (current-1);
        value = other[current];
        return value;
    }

    public T previewNext () {
        if (other.length == (current+1)) return other[0];
        int clone = current;
        return other[clone+1];
    }

    public T next () {
        if (other.length == (current+1)) {
            // Resets loop to the start
            value = other[0];
            current = 0;
            return value;
        }

        current = (current+1);
        value = other[current];
        return value;
    }

    public T get (int index) {
        return other[index];
    }

    public int getCurrent() {
        return current;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option<?> option = (Option<?>) o;
        return Objects.equals(name, option.name) &&
                Objects.equals(defaultValue, option.defaultValue);
    }
}
