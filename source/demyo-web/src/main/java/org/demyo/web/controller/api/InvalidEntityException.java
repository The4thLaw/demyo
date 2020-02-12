package org.demyo.web.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception denoting an issue with the validation of the provided entity, typically in write actions.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The provided entity is invalid")
public class InvalidEntityException extends Exception {
	private static final long serialVersionUID = -1335207167600128588L;
}
