package org.bsdevelopment.shattered.option;

public class BooleanOption extends Option<Boolean> {
    public BooleanOption(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Boolean next() {
        return setValue(!getValue());
    }
}
