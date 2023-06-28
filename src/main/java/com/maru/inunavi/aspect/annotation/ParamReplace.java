package com.maru.inunavi.aspect.annotation;

import java.lang.annotation.*;

/**
 * Replace parameter string
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParamReplace {

    String before() default "";
    String after() default "";

}
