package org.demyo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
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
	 * Ensures proper ordering of the derivatives. Relates to commit dbf3768818bb8ec1271b51e2a753c71f2d89979f .
	 */
	@Test
	@Transactional(readOnly = true)
	public void testOrdering() {
		List<Derivative> derivs = service.findPaginated(1).getContent();
		assertThat(derivs.get(0).getAlbum().getTitle()).isEqualTo("Maître d'Armes (Le)");
		assertThat(derivs.get(1).getSeries().getName()).isEqualTo("Sillage");
		assertThat(derivs.get(20).getAlbum().getTitle()).isEqualTo("Collection Privée");
	}

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
