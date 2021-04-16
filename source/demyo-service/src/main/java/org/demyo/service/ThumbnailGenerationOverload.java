package org.demyo.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;

/**
 * Exception happening due to excessing requests while generating the thumbnail, if no surrogate could be found.
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class ThumbnailGenerationOverload extends DemyoException {
	private static final long serialVersionUID = -1903648607988955726L;

	/**
	 * Creates a new exception.
	 * 
	 * @param code The code of the error.
	 * @param details Any details that could assist debugging.
	 */
	public ThumbnailGenerationOverload(String... details) {
		super(DemyoErrorCode.IMAGE_THUMBNAIL_TIMEOUT, details);
	}
}
