package org.demyo.web.controller;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.data.domain.Slice;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.demyo.model.IModel;
import org.demyo.service.IModelService;

/**
 * Base controller for management of models.
 * 
 * @param <M> The model type.
 */
public abstract class AbstractModelController<M extends IModel> extends AbstractController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelController.class);

	private final Class<M> modelClass;
	private final String urlPrefix;
	private final String modelKey;

	/**
	 * Creates the controller.
	 * 
	 * @param modelClass The concrete class of the managed model.
	 * @param urlPrefix The prefix used for URLs directed to this controller. As a convention, it is also the name of
	 *            the folder where views are stored.
	 * @param modelKey The key to store the entity in the view model.
	 */
	protected AbstractModelController(Class<M> modelClass, String urlPrefix, String modelKey) {
		this.modelClass = modelClass;
		this.urlPrefix = urlPrefix;
		this.modelKey = modelKey;
	}

	// Stub implementations for the common CRUD methods

	/**
	 * Alias for index acting on the index without slash and redirecting the user to the index with a trailing slash
	 * (and redirecting all parameters).
	 * 
	 * @param model The view model to clear.
	 * @param request The HTTP request.
	 * @param attribs The redirection attributes
	 * @return The redirection URL.
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String indexAlias(Model model, HttpServletRequest request, RedirectAttributes attribs) {
		attribs.addAllAttributes(request.getParameterMap());
		return redirect(model, "/" + urlPrefix + "/");
	}

	/**
	 * Displays a list of the entities.
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
	public String index(@RequestParam(value = "page", required = false) Integer currentPage,
			@RequestParam(value = "startsWith", required = false) Character startsWith, Model model,
			HttpServletRequest request) {
		currentPage = getCurrentPage(currentPage, startsWith);

		Slice<M> entities = getService().findPaginated(currentPage);

		model.addAttribute(modelKey + "List", entities);

		return urlPrefix + "/index";
	}

	/**
	 * Gets the actual current page.
	 * 
	 * @param currentPage The current page parameter, as passed from the request.
	 * @param startsWith The letter with which the models should start.
	 * @return The page number to actually consider.
	 */
	protected int getCurrentPage(Integer currentPage, Character startsWith) {
		if (startsWith != null) {
			return getService().getFirstPageForLetter(startsWith);
		} else if (currentPage == null) {
			return 1;
		}
		return currentPage;
	}

	/**
	 * Views the desired entity.
	 * 
	 * @param modelId The ID of the entity to view.
	 * @param model The view model.
	 * @return The view name.
	 */
	@RequestMapping(value = "/view/{modelId}", method = RequestMethod.GET)
	public String view(@PathVariable long modelId, Model model) {
		M entity = getService().getByIdForView(modelId);
		model.addAttribute(modelKey, entity);

		onView(entity, model);

		return urlPrefix + "/view";
	}

	/**
	 * Callback to implement if the controller wishes to add some information to the view action.
	 * 
	 * @param entity The entity that is going to be displayed.
	 * @param model The view model.
	 */
	protected void onView(M entity, Model model) {
		// Do nothing by default
	}

	/**
	 * Edits the desired entity.
	 * 
	 * @param modelId The ID of the entity to edit.
	 * @param model The view model.
	 * @return The view name.
	 */
	@RequestMapping(value = "/edit/{modelId}", method = RequestMethod.GET)
	public String edit(@PathVariable long modelId, Model model) {
		M entity = getService().getByIdForEdition(modelId);
		model.addAttribute(modelKey, entity);
		fillModelForEdition(entity, model);
		suppressQuickSearch(model);

		return urlPrefix + "/add-edit";
	}

	/**
	 * Adds an entity.
	 * 
	 * @param model The view model.
	 * @return The view name.
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model) {
		M entity = null;
		try {
			entity = modelClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed to instanciate model of type " + modelClass.getSimpleName(), e);
		}
		model.addAttribute(modelKey, entity);
		fillModelForEdition(entity, model);

		return urlPrefix + "/add-edit";
	}

	/**
	 * Saves changes to a new or updated entity.
	 * 
	 * @param entity The entity to save.
	 * @param result The result of the binding and validation.
	 * @param model The view model.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @return The view name.
	 */
	@RequestMapping(value = { "/add", "/edit/{modelId}" }, method = RequestMethod.POST)
	public String save(@Valid M entity, BindingResult result, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		if (result.hasErrors()) {
			LOGGER.error("There were validation errors: {}", result);
			postProcessValidationError(entity, result);
			model.addAttribute(modelKey, entity);
			fillModelForEdition(entity, model);
			return urlPrefix + "/add-edit";
		}

		long id = getService().save(entity);

		return redirect(model, "/" + urlPrefix + "/view/" + id);
	}

	/**
	 * Deletes an entity.
	 * 
	 * @param modelId The ID of the entity to delete.
	 * @param model The view model to clear.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @return The view name.
	 */
	@RequestMapping(value = { "/delete/{modelId}" }, method = RequestMethod.POST)
	public String delete(@PathVariable long modelId, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		getService().delete(modelId);
		return redirect(model, "/" + urlPrefix + "/");
	}

	// Utilities used by the stubs

	/**
	 * Callback to fill the view model with any reference data needed for creation or edition.
	 * 
	 * @param entity The entity that will be edited (can be empty, but not <code>null</code>, for creation).
	 * @param model The view model to fill.
	 */
	protected void fillModelForEdition(M entity, Model model) {

	}

	/**
	 * Callback to allow the controller to react on validation error.
	 * 
	 * @param entity The entity to save.
	 * @param result The result of the binding and validation.
	 */
	protected void postProcessValidationError(M entity, BindingResult result) {

	}

	/**
	 * If there is an error on {@code origFieldName}, this method adds exactly the same error on {@code destFieldName}.
	 * 
	 * @param result The result of the binding and validation.
	 * @param origFieldName The field name to copy the error from.
	 * @param destFieldName The field name to copy the error to.
	 */
	protected void translateError(BindingResult result, String origFieldName, String destFieldName) {
		FieldError error = result.getFieldError(origFieldName);
		if (error != null) {
			result.addError(new FieldError(error.getObjectName(), destFieldName, error.getRejectedValue(), false,
					error.getCodes(), error.getArguments(), error.getDefaultMessage()));
		}
	}

	/**
	 * Registers a {@link CustomCollectionEditor} to translate a multiple select bind value into a collection of the
	 * proper model elements.
	 * 
	 * @param binder The binder to register the editor into.
	 * @param collectionClass The class of the collection.
	 * @param propertyPath The path of the property to convert.
	 * @param collectionModelClass The class of the model in the collection.
	 * @param <C> The class of the collection.
	 */
	protected <C extends Collection<?>> void registerCollectionEditor(PropertyEditorRegistry binder,
			Class<C> collectionClass, String propertyPath, final Class<? extends IModel> collectionModelClass) {
		registerCollectionEditor(binder, collectionClass, propertyPath, collectionModelClass, null);
	}

	/**
	 * Registers a {@link CustomCollectionEditor} to translate a multiple select bind value into a sorted collection of
	 * the proper model elements.
	 * 
	 * @param binder The binder to register the editor into.
	 * @param collectionClass The class of the collection.
	 * @param propertyPath The path of the property to convert.
	 * @param collectionModelClass The class of the model in the collection.
	 * @param comparator The comparator to use for {@link SortedSet}s.
	 * @param <C> The class of the collection.
	 */
	protected <C extends Collection<?>> void registerCollectionEditor(PropertyEditorRegistry binder,
			final Class<C> collectionClass, String propertyPath, final Class<? extends IModel> collectionModelClass,
			final Comparator<?> comparator) {
		if (collectionClass.isInstance(SortedSet.class) && comparator == null) {
			throw new NullPointerException("Comparator cannot be null if the collection is sorted");
		}

		binder.registerCustomEditor(collectionClass, propertyPath, new CustomCollectionEditor(collectionClass) {
			@Override
			protected Object convertElement(Object element) {
				IModel model;
				try {
					model = collectionModelClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}

				if (element != null) {
					Long id = Long.valueOf(element.toString());
					model.setId(id);
				}
				return model;
			}

			@Override
			@SuppressWarnings({ "unchecked", "rawtypes" })
			// Also warnings in CustomCollectionEditor, do don't mind the @SuppressWarnings
			protected Collection<Object> createCollection(Class<? extends Collection> clazz, int size) {
				if (SortedSet.class.isAssignableFrom(clazz)) {
					return new TreeSet(comparator);
				}
				return super.createCollection(clazz, size);
			}
		});
	}

	/**
	 * Gets the service for this model.
	 * 
	 * @return the service.
	 */
	protected abstract IModelService<M> getService();
}
