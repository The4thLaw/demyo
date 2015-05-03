package org.demyo.web.controller;

import org.demyo.model.DerivativeType;
import org.demyo.service.IDerivativeTypeService;
import org.demyo.service.IModelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link DerivativeType} management.
 * 
 * @author $Author: xr $
 * @version $Revision: 1080 $
 */
@Controller
@RequestMapping("/derivativeTypes")
public class DerivativeTypeController extends AbstractModelController<DerivativeType> {
	@Autowired
	private IDerivativeTypeService service;

	/**
	 * Default constructor.
	 */
	public DerivativeTypeController() {
		super(DerivativeType.class, "derivativeTypes", "derivativeType");
	}

	@Override
	protected IModelService<DerivativeType> getService() {
		return service;
	}
}
