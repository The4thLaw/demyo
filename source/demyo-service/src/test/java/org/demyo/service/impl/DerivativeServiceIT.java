package org.demyo.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.model.Derivative;
import org.demyo.service.IDerivativeService;
import org.demyo.service.IDerivativeTypeService;
import org.demyo.service.ISeriesService;

/**
 * Tests for {@link DerivativeService} and for the configuration of {@link Derivative}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class DerivativeServiceIT extends AbstractServiceTest {
	@Autowired
	private IDerivativeService service;
	@Autowired
	private IDerivativeTypeService typeService;
	@Autowired
	private ISeriesService seriesService;

	/**
	 * Ensures that it is possible to save a derivative with a large number or total.
	 * 
	 * @see https://github.com/The4thLaw/demyo/issues/20
	 */
	@Test
	@Transactional
	public void testLargeDerivativeNumbers() {
		Derivative deriv = new Derivative();
		deriv.setType(typeService.getByIdForEdition(1L));
		deriv.setSeries(seriesService.getByIdForEdition(232L));
		deriv.setNumber(2_000_000_000);
		deriv.setTotal(2_000_000_000);
		service.save(deriv);
	}
}
