package org.demyo.common.exception;

import org.the4thlaw.commons.exception.CommonException;

/**
 * Demyo standard exception.
 */
public class DemyoException extends CommonException {
	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param details Any details that could assist debugging.
	 */
	public DemyoException(DemyoErrorCode code, String... details) {
		super(code, details);
	}

	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param cause The cause of the error.
	 * @param details Any details that could assist debugging.
	 */
	public DemyoException(DemyoErrorCode code, Throwable cause, String... details) {
		super(code, cause, details);
	}
}
