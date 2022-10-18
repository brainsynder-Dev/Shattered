package org.bsdevelopment.shattered.option;

import lib.brainsynder.apache.WordUtils;
import org.bsdevelopment.shattered.Shattered;

import java.text.Collator;
import java.util.LinkedList;

public class ArenaOption extends Option <String>{
    public ArenaOption(String name) {
        super(name, "RANDOM");

        LinkedList<String> list = new LinkedList<>();
        for (String string : Shattered.INSTANCE.getSchematics().getArenaMap().keySet()) {
            if (string.equals("RANDOM")) continue;
            list.addLast(WordUtils.capitalize(string));
        }
        list.sort(Collator.getInstance());
        list.addFirst("RANDOM");
        setValueList(list);
    }
}
