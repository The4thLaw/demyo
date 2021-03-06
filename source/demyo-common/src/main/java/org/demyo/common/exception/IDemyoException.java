package org.demyo.common.exception;

/**
 * Common interface for Demyo exceptions.
 */
public interface IDemyoException {

	/**
	 * Checks whether this exception matches the target code. In other words, is this exception of the expected
	 * type?
	 * 
	 * @param targetCode The code to check against.
	 * @return <code>true</code> if the exception is of the given code. <code>false</code> in all other
	 *         occurrences.
	 */
	boolean is(DemyoErrorCode targetCode);
}
