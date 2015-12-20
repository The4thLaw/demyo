package org.demyo.web.controller;

import org.demyo.model.DerivativeType;
import org.demyo.service.IDerivativeTypeService;
import org.demyo.service.IModelServiceNG;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link DerivativeType} management.
 */
@Controller
@RequestMapping("/derivativeTypes")
public class DerivativeTypeController extends AbstractModelControllerNG<DerivativeType> {
	@Autowired
	private IDerivativeTypeService service;

	/**
	 * Default constructor.
	 */
	public DerivativeTypeController() {
		super(DerivativeType.class, "derivativeTypes", "derivativeType");
	}

	@Override
	protected IModelServiceNG<DerivativeType> getService() {
		return service;
	}
}
