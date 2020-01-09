package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.DerivativeType;
import org.demyo.service.IDerivativeTypeService;

@RestController
@RequestMapping("/api/derivativeTypes")
public class DerivativeTypeAPIController extends AbstractModelAPIController<DerivativeType> {
	@Autowired
	public DerivativeTypeAPIController(IDerivativeTypeService service) {
		super(DerivativeType.class, service);
	}
}
