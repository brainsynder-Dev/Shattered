package org.bsdevelopment.shattered.option;

import org.bsdevelopment.shattered.Shattered;

import java.util.LinkedList;

public class ArenaOption extends Option <String>{
    public ArenaOption(String name) {
        super(name, "RANDOM");

        LinkedList<String> list = new LinkedList<>();
        for (String string : Shattered.INSTANCE.getSchematics().getArenaMap().values()) {
            list.addLast(string);
        }
        setValueList(list);
    }
}
