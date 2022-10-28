package org.bsdevelopment.shattered.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Permission {
    String permission() default "";
    String[] additionalPermissions() default {""};

    boolean adminCommand() default false;
}