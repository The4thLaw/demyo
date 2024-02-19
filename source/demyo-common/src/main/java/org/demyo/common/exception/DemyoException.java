package org.demyo.common.exception;

import java.util.List;

/**
 * Demyo standard exception.
 */
public class DemyoException extends Exception implements IDemyoException {
	private static final long serialVersionUID = 4065045510974057178L;

	private final DemyoErrorCode code;

	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param details Any details that could assist debugging.
	 */
	public DemyoException(DemyoErrorCode code, String... details) {
		super(toMessage(code, details));
		this.code = code;
	}

	/**
	 * Creates a new exception.
	 *
	 * @param code The code of the error.
	 * @param cause The cause of the error.
	 * @param details Any details that could assist debugging.
	 */
	public DemyoException(DemyoErrorCode code, Throwable cause, String... details) {
		super(toMessage(code, details), cause);
		this.code = code;
	}

	/**
	 * Converts the error code and potential details to a single error message.
	 *
	 * @param code The code of the error.
	 * @param details Any details that could assist debugging.
	 * @return An error message.
	 */
	/*package*/static String toMessage(DemyoErrorCode code, String... details) {
		if (code == null) {
			throw new IllegalArgumentException("Cannot create an exception with a null code");
		}
		if (details == null || details.length < 1) {
			return code.toString();
		}
		return code.toString() + " " + List.of(details);
	}

	@Override
	public boolean is(DemyoErrorCode targetCode) {
		return code == targetCode;
	}
}
