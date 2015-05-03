package org.demyo.model.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a given field as being the one to use for "startsWith" queries. There can only be one such field per
 * class. If more than one is specified, results are indeterminate.
 * 
 * @author $Author: xr $
 * @version $Revision: 1076 $
 */
@Documented
@Target(FIELD)
@Retention(RUNTIME)
public @interface StartsWithField {
}
