package org.bsdevelopment.shattered.command.annotations;

import java.lang.annotation.Repeatable;

@Repeatable(UsageContainer.class)
public @interface AdditionalUsage {
    String name() default "";
    String usage() default "";
    String description() default "";

    boolean checkPermission () default false;
}