package org.demyo.web.controller.api;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.demyo.model.IModel;
import org.demyo.model.ModelView;
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
	@RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
	@ResponseBody
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		List<M> value = getService().findAll();
		MappingJacksonValue jackson = new MappingJacksonValue(value);
		jackson.setSerializationView(ModelView.byName(view));
		return jackson;
	}

	/**
	 * Gets the service for this model.
	 * 
	 * @return the service.
	 */
	protected abstract IModelService<M> getService();
}
