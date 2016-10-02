package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.demyo.web.test.mvc.WebDriverAssertions.assertThat;

import java.util.List;

import org.demyo.web.controller.AlbumController;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Integration tests for {@link AlbumController}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class AlbumControllerIT extends AbstractMvcTest {

	/**
	 * Tests that the information on the view page is correct.
	 */
	@Test
	public void testViewPage() {
		getWebDriver().get("http://localhost/albums/view/1");

		// Check the Album name
		assertAlbumTitle("3 - Toutes les larmes de l'enfer");

		// Check the Series name
		assertThat(css1("#album-identification :nth-child(2) .dem-form__value a")).textContent()
				.isEqualToIgnoringWhitespace("XIII");

		// Check the Tag names
		List<WebElement> tags = cssM(".dem-tag__name");
		assertThat(tags).hasSize(2);
		assertThat(tags.get(0)).textContent().isEqualToIgnoringWhitespace("action");
		assertThat(tags.get(1)).textContent().isEqualToIgnoringWhitespace("science-fiction");
	}

	/**
	 * Tests adding an Album.
	 */
	@Test
	public void testAddPage() {
		getWebDriver().get("http://localhost/albums/add");

		// Set a title
		css1("#field_album_title").sendKeys("My new album");

		// Add prices
		assertThat(cssM(".dem-repeatable-item")).isEmpty();
		// First price
		css1("#prices-adder").click();
		assertThat(cssM(".dem-repeatable-item")).hasSize(1);
		setFieldValue("field_album_priceList[0]_date", "2011-01-01");
		css1("#field_album_priceList\\[0\\]_price").sendKeys("10");
		// Second price
		css1("#prices-adder").click();
		assertThat(cssM(".dem-repeatable-item")).hasSize(2);
		setFieldValue("field_album_priceList[1]_date", "2010-01-01");
		css1("#field_album_priceList\\[1\\]_price").sendKeys("11");

		submitMainModelForm();

		assertThat(getWebDriver().getCurrentUrl()).startsWith("http://localhost/albums/view/");

		assertAlbumTitle("My new album");
	}

	/**
	 * Tests editing an album.
	 */
	@Test
	public void testEditPage() {
		getWebDriver().get("http://localhost/albums/edit/1");

		assertThat(css1("#field_album_priceList\\[0\\]_date")).hasAttributeEqualTo("value", "2016-01-10");
		assertThat(css1("#field_album_priceList\\[0\\]_price")).hasAttributeEqualTo("value", "10.0");
		assertThat(css1("#field_album_priceList\\[1\\]_date")).hasAttributeEqualTo("value", "2016-01-15");
		assertThat(css1("#field_album_priceList\\[1\\]_price")).hasAttributeEqualTo("value", "1.5");

		submitMainModelForm();

		assertThat(getWebDriver().getCurrentUrl()).isEqualTo("http://localhost/albums/view/1");

		// Check the Album name
		assertAlbumTitle("3 - Toutes les larmes de l'enfer");
	}

	private void assertAlbumTitle(String title) {
		assertThat(css1("#album-identification :nth-child(1) .dem-form__value")).textContent()
				.isEqualToIgnoringWhitespace(title);
	}
}
