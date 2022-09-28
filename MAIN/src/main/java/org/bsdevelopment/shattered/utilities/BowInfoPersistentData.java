package org.bsdevelopment.shattered.utilities;

import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class BowInfoPersistentData implements PersistentDataType<String, BowInfo> {
    public static final BowInfoPersistentData INSTANCE = new BowInfoPersistentData();

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<BowInfo> getComplexType() {
        return BowInfo.class;
    }

    @Override
    public String toPrimitive(BowInfo shop, PersistentDataAdapterContext context) {
        return shop.toCompound().toString();
    }

    @Override
    public BowInfo fromPrimitive(String string, PersistentDataAdapterContext context) {
        return new BowInfo(string);
    }

}