package org.bsdevelopment.shattered.bow.tasks;

import org.bsdevelopment.shattered.bow.data.BowInfo;

public interface RemoveTask {
    /**
     * This function is called when the arrow is removed from existence
     *      EG: When the arrow flies out of the map/hits the ground/removed(killed)
     *
     * @param info The BowInfo object that contains all the information about the bow.
     */
    void onRemove(BowInfo info);
}
