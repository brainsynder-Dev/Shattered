package org.bsdevelopment.shattered.utilities;

public enum MessageType {
    MESSAGE ("&#5676D7[&#8FA5E5Shattered&#5676D7] &#c8cad0"),
    ERROR ("&#5676D7[&#8FA5E5Shattered&#5676D7] &#de9790"),
    NO_PREFIX ("&#c8cad0"),

    SHATTERED_BLUE ("&#8FA5E5"),
    SHATTERED_GRAY ("&#c8cad0"),
    SHATTERED_DARK_BLUE ("&#5676D7"),
    SHATTERED_RED ("&#de9790");

    private final String prefix;

    MessageType (String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }
}
