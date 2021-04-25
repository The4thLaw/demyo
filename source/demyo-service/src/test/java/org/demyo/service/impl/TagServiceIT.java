package org.demyo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.model.Tag;
import org.demyo.service.ITagService;

/**
 * Tests for {@link TagService}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class TagServiceIT extends AbstractServiceTest {
	@Autowired
	private ITagService tagService;

	@Test
	void testGetByIdForView() {
		Tag tag = tagService.getByIdForView(1);
		assertThat(tag.getId()).isEqualTo(1);
		assertThat(tag.getName()).isEqualTo("science-fiction");
	}

	@Test
	void testSave() {
		Tag tagToSave = new Tag();
		tagToSave.setId(21L);
		tagToSave.setName("one shot");
		tagToSave.setBgColour("000000");
		tagService.save(tagToSave);

		Tag savedTag = tagService.getByIdForView(21L);
		assertThat(savedTag.getId()).isEqualTo(21L);
		assertThat(savedTag.getBgColour()).isEqualTo("000000");
	}
}
