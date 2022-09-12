package org.bsdevelopment.shattered.files;

import lib.brainsynder.files.StorageFile;
import org.bsdevelopment.shattered.Shattered;

import java.io.File;

public class DataStorage extends StorageFile {
    public DataStorage(Shattered shattered) {
        super(new File(shattered.getDataFolder(), "data-storage.nbt"));
    }
}
