package com.hypixel.hytale.server.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock Hytale Plugin Annotation
 * TEMPORARY: Replace with official Hytale API when available
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HytalePlugin {
    String id();
    String name();
    String version();
    String[] authors() default {};
    String description() default "";
}
