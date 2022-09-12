package org.bsdevelopment.shattered.bow.annotations;

import org.bsdevelopment.shattered.bow.data.BowType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BowData {
    /**
     * The name of the bow
     */
    String name ();

    /**
     * Describe what the bows does
     */
    String[] description ();

    /**
     * What type(s) of bow is this labeled as?
     */
    BowType[] type ();


    /**
     * How many uses should the bow be allowed to have by default
     */
    int defaultUses() default 1;

    /**
     * Does the bow have a custom model?
     */
    int customModelData() default -1;

    /**
     * What is the chance that the bow will spawn on the map
     */
    double spawnChance() default 40;

    /**
     * Should the arrow fired from the bow be immediately removed when it hits something
     */
    boolean removeArrowOnHit() default true;
}
