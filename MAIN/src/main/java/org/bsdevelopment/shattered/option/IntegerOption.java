package org.bsdevelopment.shattered.option;

public class IntegerOption extends Option<Integer> {
    public IntegerOption(String displayName, int defaultValue) {
        super(displayName, defaultValue, 0);
    }
    public IntegerOption(String displayName, int defaultValue, Integer... allValues) {
        super(displayName, defaultValue, allValues);
    }

    public void increase () {
        setValue(getValue()+1);
    }

    public void decrease () {
        setValue(getValue()-1);
    }
}
