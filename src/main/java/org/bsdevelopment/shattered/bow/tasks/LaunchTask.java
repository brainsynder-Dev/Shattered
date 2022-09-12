package org.bsdevelopment.shattered.bow.tasks;

import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bukkit.entity.Arrow;

public interface LaunchTask {
    void onLaunch(Arrow arrow, BowInfo info);
}
