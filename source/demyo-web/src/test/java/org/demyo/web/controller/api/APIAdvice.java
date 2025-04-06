package org.demyo.web.controller.api;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller advice to handle exception on API methods.
 */
@ControllerAdvice("org.demyo.web.controller.api")
public class APIAdvice {
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler({ EntityNotFoundException.class })
	public void handle(EntityNotFoundException e) {
		// Nothing to do, the status is sent by the annotation
	}
}
