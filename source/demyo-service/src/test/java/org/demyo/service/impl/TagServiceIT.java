package org.demyo.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.model.Taxon;
import org.demyo.service.ITaxonService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TaxonService}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class TagServiceIT extends AbstractServiceTest {
	@Autowired
	private ITaxonService tagService;

	@Test
	void testGetByIdForView() {
		Taxon tag = tagService.getByIdForView(1);
		assertThat(tag.getId()).isEqualTo(1);
		assertThat(tag.getName()).isEqualTo("science-fiction");
	}

	@Test
	void testSave() {
		Taxon tagToSave = new Taxon();
		tagToSave.setId(21L);
		tagToSave.setName("one shot");
		tagToSave.setBgColour("#000000");
		tagService.save(tagToSave);

		Taxon savedTag = tagService.getByIdForView(21L);
		assertThat(savedTag.getId()).isEqualTo(21L);
		assertThat(savedTag.getBgColour()).isEqualTo("#000000");
	}
}
