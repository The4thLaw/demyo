package org.demyo.web.controller.api;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.demyo.model.IModel;
import org.demyo.model.ModelView;
import org.demyo.service.IModelService;

@ResponseBody
public abstract class AbstractModelAPIController<M extends IModel> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelAPIController.class);

	private final Class<M> modelClass;
	private final IModelService<M> service;

	/**
	 * Creates the controller.
	 * 
	 * @param modelClass The concrete class of the managed model.
	 * @param service The service to manage the model.
	 */
	protected AbstractModelAPIController(Class<M> modelClass, IModelService<M> service) {
		this.modelClass = modelClass;
		this.service = service;
	}

	/**
	 * Retrieves the full list of the entities.
	 * 
	 * @param view The Jackson view to apply.
	 * @return The list.
	 */
	@GetMapping({ "/", "/index" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		List<M> value = service.findAll();
		return getIndexView(view, value);
	}

	protected MappingJacksonValue getIndexView(Optional<String> view, List<M> value) {
		MappingJacksonValue jackson = new MappingJacksonValue(value);
		Optional<Class<?>> viewClass = ModelView.byName(view);
		LOGGER.debug("View class is {}", viewClass);
		if (viewClass.isPresent()) {
			jackson.setSerializationView(viewClass.get());
		}
		return jackson;
	}

	@GetMapping("/{modelId}")
	public M view(@PathVariable long modelId) {
		return service.getByIdForView(modelId);
	}

	/**
	 * Saves changes to a new or updated entity.
	 * 
	 * @param entity The entity to save.
	 * @param result The result of the binding and validation.
	 * @return The internal ID of the saved entity.
	 */
	@RequestMapping(value = { "/", "/{modelId}" }, method = { RequestMethod.POST, RequestMethod.PUT })
	public long save(@RequestBody @Valid M entity, BindingResult result) {
		LOGGER.debug("Requested to save entity: {}", entity);
		if (result.hasErrors()) {
			LOGGER.error("There were validation errors: {}", result);
			return -1;
		}

		return service.save(entity);
	}

	/**
	 * Deletes an entity.
	 * 
	 * @param modelId The ID of the entity to delete.
	 * @return Always <code>true</code>.
	 */
	@DeleteMapping("/{modelId}")
	public boolean delete(@PathVariable long modelId) {
		service.delete(modelId);
		return true;
	}
}
