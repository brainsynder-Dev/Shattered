package org.bsdevelopment.shattered.option;

import java.util.LinkedList;

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

    public static LinkedList<Integer> range (int start, int end, int increment) {
        LinkedList<Integer> list = new LinkedList<>();

        start = (start - 1);

        while (start < end) {
            start = (start + increment);
            if (start > end) break;
            list.addLast(start);
        }
        return list;
    }
}
