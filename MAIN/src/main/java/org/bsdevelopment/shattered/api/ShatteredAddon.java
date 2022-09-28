package org.bsdevelopment.shattered.api;

/**
 * `ShatteredAddon` is the base class for all addons
 */
public abstract class ShatteredAddon {
    /**
     * This function is called when the addon is loaded
     */
    public abstract void initiate ();

    /**
     * This function is called when the addon is unloaded.
     */
    public abstract void cleanup ();

    /**
     * If the class has the `@Namespace` annotation, return it. If not, throw a `NullPointerException` with a warning message
     *
     * @return The Namespace annotation for the addon.
     */
    public Namespace getNamespace() {
        if (getClass().isAnnotationPresent(Namespace.class)) return getClass().getAnnotation(Namespace.class);
        throw new NullPointerException(getClass().getSimpleName() + " is missing @Namespace annotation for the addon");
    }
}
