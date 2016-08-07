package org.demyo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.demyo.model.Derivative;
import org.demyo.service.IDerivativeService;
import org.demyo.service.impl.DerivativeService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Tests for {@link DerivativeService} and for the configuration of {@link Derivative}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class DerivativeServiceIT extends AbstractServiceTest {
	@Autowired
	private IDerivativeService service;

	/**
	 * Ensures proper ordering of the derivatives. Relates to commit dbf3768818bb8ec1271b51e2a753c71f2d89979f .
	 */
	@Test
	@Transactional(readOnly = true)
	public void testOrdering() {
		List<Derivative> derivs = service.findPaginated(1).getContent();
		assertThat(derivs.get(0).getAlbum().getTitle()).isEqualTo("Bludzee");
		assertThat(derivs.get(1).getSeries().getName()).isEqualTo("Sillage");
		assertThat(derivs.get(2).getSeries().getName()).isEqualTo("XIII");
		assertThat(derivs.get(2).getAlbum().getTitle()).isEqualTo("Toutes les larmes de l'enfer");
	}
}
