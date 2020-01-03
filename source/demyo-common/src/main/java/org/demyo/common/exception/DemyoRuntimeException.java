package org.demyo.common.exception;

/**
 * Demyo unchecked exception.
 */
public class DemyoRuntimeException extends RuntimeException implements IDemyoException {
	private static final long serialVersionUID = -1903648607988955726L;

	private final DemyoErrorCode code;

	/**
	 * Creates a new exception.
	 * 
	 * @param code The code of the error.
	 * @param details Any details that could assist debugging.
	 */
	public DemyoRuntimeException(DemyoErrorCode code, String... details) {
		super(DemyoException.toMessage(code, details));
		this.code = code;
	}

	/**
	 * Creates a new exception.
	 * 
	 * @param code The code of the error.
	 * @param cause The cause of the error.
	 * @param details Any details that could assist debugging.
	 */
	public DemyoRuntimeException(DemyoErrorCode code, Throwable cause, String... details) {
		super(DemyoException.toMessage(code, details), cause);
		this.code = code;
	}

	@Override
	public boolean is(DemyoErrorCode targetCode) {
		return code == targetCode;
	}
}
