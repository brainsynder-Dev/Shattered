package org.bsdevelopment.shattered.bow.tasks;

import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;

public interface AirTask {
    void whileInAir(Arrow arrow, BowInfo info, Location currentLocation);
}