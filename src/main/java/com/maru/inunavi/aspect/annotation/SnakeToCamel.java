package com.maru.inunavi.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Convert snake_case to camelCase on controller method
 * @see com.maru.inunavi.aspect.filter.SnakeToCamelFilter.RequestWrapper
 * @see org.springframework.web.util.WebUtils getParametersStartingWith()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SnakeToCamel {
}
