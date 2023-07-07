package com.maru.inunavi.aspect.annotation;

import java.lang.annotation.*;

/**
 * Replace parameter string
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReplaceParameter {

    String before() default "";
    String after() default "";

}
