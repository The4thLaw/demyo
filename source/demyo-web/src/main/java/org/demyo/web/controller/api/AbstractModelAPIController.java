package org.demyo.web.controller.api;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
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
	 * @param view The Jackson view to apply.
	 * @return The list.
	 */
	@GetMapping({ "/", "/index" })
	@ResponseBody
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		List<M> value = getService().findAll();
		MappingJacksonValue jackson = new MappingJacksonValue(value);
		Optional<Class<?>> viewClass = ModelView.byName(view);
		LOGGER.debug("View class is {}", viewClass);
		if (viewClass.isPresent()) {
			jackson.setSerializationView(viewClass.get());
		}
		return jackson;
	}

	/**
	 * Gets the service for this model.
	 * 
	 * @return the service.
	 */
	protected abstract IModelService<M> getService();
}