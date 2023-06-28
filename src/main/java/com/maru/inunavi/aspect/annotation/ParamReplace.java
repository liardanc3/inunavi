package com.maru.inunavi.aspect.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParamReplace {

    String before() default "";
    String after() default "";

}
