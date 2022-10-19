package org.bsdevelopment.shattered.option;

import java.util.LinkedList;
import java.util.Objects;

public class Option<T> {
    private final LinkedList<T> VALUE_STORAGE;
    private final String name;
    private T defaultValue;

    private T value;

    private String callerName;
    private String description;

    public Option(String name, T defaultValue, T... allValues) {
        this.name = name;
        VALUE_STORAGE = new LinkedList<>();

        this.defaultValue = defaultValue;
        for (T t : allValues) {
            VALUE_STORAGE.addLast(t);
        }
    }

    /**
     * This function takes a collection of objects and adds them to the end of the linked list.
     *
     * @param collection The collection of values to be added to the list.
     */
    public Option<T> setValueList (LinkedList<T> collection) {
        VALUE_STORAGE.clear();

        for (T t : collection) {
            VALUE_STORAGE.addLast(t);
        }
        return this;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * "This function sets the description of the option and returns the option."
     *
     * The above function is a fluent interface
     *
     * @param description The description of the option.
     * @return The Option<T> object itself.
     */
    public Option<T> setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * This function sets the callerName variable to the value of the callerName parameter.
     *
     * @param callerName The name of the caller.
     */
    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    /**
     * This function returns the caller's name
     *
     * @return The callerName variable is being returned.
     */
    public String getCallerName() {
        return callerName;
    }

    /**
     * Returns the default value of the property.
     *
     * @return The default value of the field.
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * > This function returns the description of the object
     *
     * @return The description of the item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * This function returns the name of the Option.
     *
     * @return The name of the person.
     */
    public String getName() {
        return name;
    }

    public String getCombinedName() {
        return callerName+"-"+getStorageName();
    }

    /**
     * Replace all spaces in the name with underscores and convert the name to lowercase.
     *
     * @return The name of the storage, with spaces replaced by underscores and all letters in lowercase.
     */
    public String getStorageName () {
        return name.replace(" ", "_").toLowerCase();
    }

    /**
     * If the value is null, return the default value, otherwise return the value.
     *
     * @return The value of the variable value.
     */
    public T getValue() {
        if (value == null) return defaultValue;
        return value;
    }

    /**
     * Sets the value of the node to the value passed in and returns the value.
     *
     * @param value The value of the node.
     * @return The value of the node.
     */
    public T setValue(T value) {
        return this.value = value;
    }

    /**
     * If the current value is not the first value in the list, return the previous value in the list, otherwise return the
     * last value in the list
     *
     * @return The previous value in the list.
     */
    public T previous () {
        for (T current: VALUE_STORAGE) {
            if (current != getValue()) continue;

            try {
                return value = VALUE_STORAGE.get(VALUE_STORAGE.indexOf(current)-1);
            } catch (IndexOutOfBoundsException ignored){
                return value = VALUE_STORAGE.peekLast();
            }
        }
        return value;
    }

    /**
     * If the current value is not the first value in the list, return the previous value, otherwise return the last value.
     *
     * @return The previous value in the list.
     */
    public T previewPrevious () {
        for (T current: VALUE_STORAGE) {
            if (current != getValue()) continue;

            try {
                return VALUE_STORAGE.get(VALUE_STORAGE.indexOf(current)-1);
            } catch (IndexOutOfBoundsException ignored){}
        }
        return VALUE_STORAGE.peekLast();
    }

    /**
     * If the current value is not the last value in the list, return the next value in the list. If the current value is
     * the last value in the list, return the first value in the list
     *
     * @return The next value in the VALUE_STORAGE.
     */
    public T next () {
        for (T current: VALUE_STORAGE) {
            if (current != getValue()) continue;

            try {
                return value = VALUE_STORAGE.get(VALUE_STORAGE.indexOf(current)+1);
            } catch (IndexOutOfBoundsException ignored){}
        }
        return value = VALUE_STORAGE.peekFirst();
    }

    /**
     * If the current value is not the last value in the list, return the next value in the list, otherwise return the
     * first value in the list.
     *
     * @return The next value in the list.
     */
    public T previewNext () {
        for (T current: VALUE_STORAGE) {
            if (current != getValue()) continue;

            try {
                return VALUE_STORAGE.get(VALUE_STORAGE.indexOf(current)+1);
            } catch (IndexOutOfBoundsException ignored){}
        }
        return VALUE_STORAGE.peekFirst();
    }

    /**
     * Get the value at the given index.
     *
     * @param index The index of the element to return.
     * @return The value at the index.
     */
    public T get (int index) {
        return VALUE_STORAGE.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option<?> option = (Option<?>) o;
        return Objects.equals(name, option.name) &&
                Objects.equals(defaultValue, option.defaultValue);
    }

    @Override
    public String toString() {
        return "Option{name='%s', callerName='%s'}".formatted(name, callerName);
    }
}
