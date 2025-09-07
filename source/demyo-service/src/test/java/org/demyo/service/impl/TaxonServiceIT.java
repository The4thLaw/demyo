package org.demyo.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.model.Taxon;
import org.demyo.model.enums.TaxonType;
import org.demyo.service.ITaxonService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TaxonService}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class TaxonServiceIT extends AbstractServiceTest {
	@Autowired
	private ITaxonService taxonService;

	@Test
	void testGetByIdForView() {
		Taxon taxon = taxonService.getByIdForView(1);
		assertThat(taxon.getId()).isEqualTo(1);
		assertThat(taxon.getName()).isEqualTo("science-fiction");
	}

	@Test
	void testSave() {
		Taxon taxonToSave = new Taxon();
		taxonToSave.setId(21L);
		taxonToSave.setName("one shot");
		taxonToSave.setBgColour("#000000");
		taxonToSave.setType(TaxonType.TAG);
		taxonService.save(taxonToSave);

		Taxon savedTaxon = taxonService.getByIdForView(21L);
		assertThat(savedTaxon.getId()).isEqualTo(21L);
		assertThat(savedTaxon.getBgColour()).isEqualTo("#000000");
	}
}
