package org.demyo.model.exception;


/**
 * Demyo unchecked exception.
 * 
 * @author $Author: xr $
 * @version $Revision: 1059 $
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
		if (code == null) {
			throw new IllegalArgumentException("Cannot create an exception with a null code");
		}
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
		if (code == null) {
			throw new IllegalArgumentException("Cannot create an exception with a null code");
		}
		this.code = code;
	}

	@Override
	public boolean is(DemyoErrorCode targetCode) {
		return code.equals(targetCode);
	}
}
