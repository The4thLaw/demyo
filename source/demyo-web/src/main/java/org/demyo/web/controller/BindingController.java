package org.demyo.web.controller;

import org.demyo.model.Binding;
import org.demyo.service.IBindingService;
import org.demyo.service.IModelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link Binding} management.
 */
@Controller
@RequestMapping("/bindings")
public class BindingController extends AbstractModelController<Binding> {
	@Autowired
	private IBindingService service;

	/**
	 * Default constructor.
	 */
	public BindingController() {
		super(Binding.class, "bindings", "binding");
	}

	@Override
	protected IModelService<Binding> getService() {
		return service;
	}
}
