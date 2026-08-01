package org.demyo.common.exception;

import org.the4thlaw.commons.exception.CommonRuntimeException;

/**
 * Demyo unchecked exception.
 */
public class DemyoRuntimeException extends CommonRuntimeException {
	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param details Any details that could assist debugging.
	 */
	public DemyoRuntimeException(DemyoErrorCode code, String... details) {
		super(code, details);
	}

	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param cause The cause of the error.
	 * @param details Any details that could assist debugging.
	 */
	public DemyoRuntimeException(DemyoErrorCode code, Throwable cause, String... details) {
		super(code, cause, details);
	}
}
