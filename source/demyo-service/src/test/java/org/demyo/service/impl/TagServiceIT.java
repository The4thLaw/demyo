package org.demyo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.demyo.model.Tag;
import org.demyo.service.AbstractServiceTest;
import org.demyo.service.ITagService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Tests for {@link TagService}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class TagServiceIT extends AbstractServiceTest {
	@Autowired
	private ITagService tagService;

	@Test
	public void testGetByIdForView() {
		Tag tag = tagService.getByIdForView(1);
		assertThat(tag.getId()).isEqualTo(1);
		assertThat(tag.getName()).isEqualTo("action");
		assertThat(tag.getTaggedAlbums().size()).isEqualTo(1);
		assertThat(tag.getTaggedAlbums().first().getTitle()).isEqualTo("Toutes les larmes de l'enfer");
	}

	@Test
	public void testSave() {
		Tag tagToSave = new Tag();
		tagToSave.setId(1L);
		tagToSave.setName("action");
		tagToSave.setBgColour("000000");
		tagService.save(tagToSave);

		Tag savedTag = tagService.getByIdForView(1);
		assertThat(savedTag.getId()).isEqualTo(1);
		assertThat(savedTag.getBgColour()).isEqualTo("000000");
		// Check that the association was preserved even though we don't set the albums
		assertThat(savedTag.getTaggedAlbums()).isNotNull();
		assertThat(savedTag.getTaggedAlbums().size()).isEqualTo(1);
		assertThat(savedTag.getTaggedAlbums().first().getTitle()).isEqualTo("Toutes les larmes de l'enfer");
	}
}
