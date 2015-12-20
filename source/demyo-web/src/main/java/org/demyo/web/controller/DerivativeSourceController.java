package org.demyo.web.controller;

import org.demyo.model.DerivativeSource;
import org.demyo.service.IDerivativeSourceService;
import org.demyo.service.IModelServiceNG;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link DerivativeSource} management.
 */
@Controller
@RequestMapping("/derivativeSources")
public class DerivativeSourceController extends AbstractModelControllerNG<DerivativeSource> {
	@Autowired
	private IDerivativeSourceService service;

	/**
	 * Default constructor.
	 */
	public DerivativeSourceController() {
		super(DerivativeSource.class, "derivativeSources", "derivativeSource");
	}

	@Override
	protected IModelServiceNG<DerivativeSource> getService() {
		return service;
	}
}
