package org.demyo.web.controller;

import org.demyo.model.DerivativeSource;
import org.demyo.service.IDerivativeSourceService;
import org.demyo.service.IModelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link DerivativeSource} management.
 * 
 * @author $Author: xr $
 * @version $Revision: 1080 $
 */
@Controller
@RequestMapping("/derivativeSources")
public class DerivativeSourceController extends AbstractModelController<DerivativeSource> {
	@Autowired
	private IDerivativeSourceService service;

	/**
	 * Default constructor.
	 */
	public DerivativeSourceController() {
		super(DerivativeSource.class, "derivativeSources", "derivativeSource");
	}

	@Override
	protected IModelService<DerivativeSource> getService() {
		return service;
	}
}
