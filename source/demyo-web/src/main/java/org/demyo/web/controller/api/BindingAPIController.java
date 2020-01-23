package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Binding;
import org.demyo.service.IBindingService;

/**
 * Controller handling the API calls for {@link Binding}s.
 */
@RestController
@RequestMapping("/api/bindings")
public class BindingAPIController extends AbstractModelAPIController<Binding> {
	@Autowired
	public BindingAPIController(IBindingService service) {
		super(Binding.class, service);
	}
}
