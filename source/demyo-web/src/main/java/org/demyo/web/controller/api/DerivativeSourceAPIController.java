package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.DerivativeSource;
import org.demyo.service.IDerivativeSourceService;

@RestController
@RequestMapping("/api/derivativeSources")
public class DerivativeSourceAPIController extends AbstractModelAPIController<DerivativeSource> {
	@Autowired
	public DerivativeSourceAPIController(IDerivativeSourceService service) {
		super(DerivativeSource.class, service);
	}
}
