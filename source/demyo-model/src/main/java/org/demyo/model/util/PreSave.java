package org.demyo.model.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a method as having to be called before performing save operations. Typical use case: convert data coming
 * from MVC to data that the DB can handle.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface PreSave {

}
