package org.bsdevelopment.shattered.bow.data;

public enum BowForce {
    LOW,
    MEDIUM,
    MAX;

    public static BowForce getForce (float force) {
        if (force > 0.8) {
            return MAX;
        }else if ((force <= 0.8) && (force >= 0.5)) {
            return MEDIUM;
        }
        return LOW;
    }
}
