package org.demyo.test.utils;

/**
 * Defines a generic predicate.
 * <p>
 * Mainly used as a transition while waiting for Java 8 as standard.
 * </p>
 */
public interface Predicate {
	/**
	 * Tests the predicate.
	 * 
	 * @return <code>true</code> if the predicate is valid.
	 */
	boolean test();
}
