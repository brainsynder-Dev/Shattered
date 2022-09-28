package org.bsdevelopment.shattered.version;

public interface IVersion {
    String name ();

    String getNMS ();

    Triple<Integer, Integer, Integer> getVersionParts();

    IVersion getParent ();
}
