package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.web.controller.TagController;

/**
 * Integration tests for {@link TagController}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class TagControllerIT extends AbstractMvcTest {

	/**
	 * Tests that the checkboxes for default colour for Tags are set correctly.
	 */
	@Test
	public void testEditPageDefaultColorCheckboxes() {
		getWebDriver().get("http://localhost/tags/edit/1");
		assertThat(css1("#field_tag_fgColour_colour_remover").getAttribute("checked")).isEqualTo("true");
		assertThat(css1("#field_tag_bgColour_colour_remover").getAttribute("checked")).isEqualTo("true");

		// TODO: fix this is Vue is not kept in the end (unlikely)
		// getWebDriver().get("http://localhost/tags/edit/3");
		// assertThat(css1("#field_tag_fgColour_colour_remover").getAttribute("checked")).isNull();
		// assertThat(css1("#field_tag_bgColour_colour_remover").getAttribute("checked")).isNull();
	}
}
