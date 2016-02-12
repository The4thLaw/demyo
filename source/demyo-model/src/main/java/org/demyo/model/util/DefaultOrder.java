package org.demyo.model.util;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies a default order for find queries.
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface DefaultOrder {
	/** Specification of a single order clause. */
	public @interface Order {
		/** The property to sort on. */
		String property();

		/**
		 * The direction of the sort: <code>true</code> if the sort is ascending, <code>false</code> if it is
		 * descending. Defaults to <code>true</code>.
		 */
		boolean asc() default true;
	}

	/**
	 * The expression to use to sort find queries by default.
	 */
	Order[] expression();
}
