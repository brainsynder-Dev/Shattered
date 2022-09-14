package org.bsdevelopment.shattered.bow.tasks;

import org.bsdevelopment.shattered.bow.data.BowInfo;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public interface HitPlayerTask {
    void onHit(Arrow arrow, BowInfo info, Player target);
}
