package org.demyo.web.controller.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.demyo.model.IModel;
import org.demyo.service.IModelService;

public abstract class AbstractModelAPIController<M extends IModel> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelAPIController.class);

	private final Class<M> modelClass;

	/**
	 * Creates the controller.
	 * 
	 * @param modelClass The concrete class of the managed model.
	 */
	protected AbstractModelAPIController(Class<M> modelClass) {
		this.modelClass = modelClass;
	}

	/**
	 * Retrieves the full list of the entities.
	 * 
	 * @param currentPage The current page number (starting from 1). Can be missing.
	 * @param model The view model.
	 * @param startsWith The letter to which to jump.
	 * @param request the HTTP request (e.g. for direct access to parameters).
	 * @return The view name.
	 */
	// We need to provide direct access to the request because we cannot vary the mapping based on optional request
	// parameters
	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	@ResponseBody
	public List<M> index() {
		return getService().findAll();
	}

	/**
	 * Gets the service for this model.
	 * 
	 * @return the service.
	 */
	protected abstract IModelService<M> getService();
}
