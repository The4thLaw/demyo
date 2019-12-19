package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.demyo.web.test.mvc.WebDriverAssertions.assertThat;

import java.util.List;
import java.util.regex.Pattern;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.web.controller.AlbumController;

/**
 * Integration tests for {@link AlbumController}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class AlbumControllerIT extends AbstractMvcTest {
	private static final String NEW_ALBUM_TITLE = "My new album";

	/**
	 * Tests that the index is complete and in the right order.
	 */
	@Test
	public void testIndex() { // Will run after add
		getWebDriver().get("http://localhost/albums/");

		List<WebElement> titles = cssM(".dem-model-card__title");
		assertThat(titles).hasSize(4);
		int i = 0;
		assertThat(titles.get(i++)).textContent().isEqualToIgnoringWhitespace("Maître d'Armes (Le)");
		assertThat(titles.get(i++)).textContent().isEqualToIgnoringWhitespace(NEW_ALBUM_TITLE);
		assertThat(titles.get(i++)).textContent().isEqualToIgnoringWhitespace("Sillage");
		assertThat(titles.get(i++)).textContent().isEqualToIgnoringWhitespace("Sillage - Premières Armes");
	}

	/**
	 * Tests that the information on the view page is correct.
	 */
	@Test
	@Ignore("Buggy, due to be phased out in switch to Vue")
	public void testViewPage() {
		getWebDriver().get("http://localhost/albums/view/435");

		// Check the Album name
		assertAlbumTitle("1ré - À Feu et à Cendres");

		// Check the Series name
		assertThat(css1("#album-identification :nth-child(2) .dem-form__value a")).textContent()
				.isEqualToIgnoringWhitespace("Sillage");

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
		waitForRepeatablePartInit();

		// Set a title
		css1("#field_album_title").sendKeys(NEW_ALBUM_TITLE);

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

		assertAlbumTitle(NEW_ALBUM_TITLE);
	}

	/**
	 * Tests that the relevant errors are displayed when saving an empty Album.
	 */
	@Test
	// We must start by clearing the publishers so that there is no default value
	@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.DELETE_ALL)
	public void testAddEmptyAlbum() {
		getWebDriver().get("http://localhost/albums/add");
		waitForRepeatablePartInit();

		submitMainModelForm();

		assertThat(getWebDriver().getCurrentUrl()).startsWith("http://localhost/albums/add");

		String pageSource = getWebDriver().getPageSource();
		assertThat(pageSource).matches(Pattern.compile(
				".*Title.*?</label>.*?<span class=\"" + "mdl-textfield__error\">.*?This field cannot be empty.*",
				Pattern.DOTALL));
		assertThat(pageSource).matches(Pattern.compile(
				".*Publisher.*?</label>.*?<span class=\"" + "mdl-textfield__error\">.*?This field cannot be empty.*",
				Pattern.DOTALL));
	}

	/**
	 * Tests editing an album.
	 */
	@Test
	@Ignore("Buggy, due to be phased out in switch to Vue")
	public void testEditPage() {
		getWebDriver().get("http://localhost/albums/edit/764");
		waitForRepeatablePartInit();

		assertThat(css1("#field_album_priceList\\[0\\]_date")).hasAttributeEqualTo("value", "2010-09-26");
		assertThat(css1("#field_album_priceList\\[0\\]_price")).hasAttributeEqualTo("value", "200.0");
		assertThat(css1("#field_album_priceList\\[1\\]_date")).hasAttributeEqualTo("value", "2011-09-26");
		assertThat(css1("#field_album_priceList\\[1\\]_price")).hasAttributeEqualTo("value", "150.0");

		submitMainModelForm();

		assertThat(getWebDriver().getCurrentUrl()).isEqualTo("http://localhost/albums/view/1");

		// Check the Album name
		assertAlbumTitle("3 - Toutes les larmes de l'enfer");
	}

	/**
	 * Tests that it is possible to add an album to an empty Series.
	 * 
	 * @see commit b01c5e7240866ee82ccb3dd6928f0fc245381754
	 */
	@Test
	public void testAddToEmptySeries() {
		getWebDriver().get("http://localhost/series/view/2");

		// First, ensure the Series is really empty. Else, it doesn't make sense
		List<WebElement> matches = cssM(".dem-album-card");
		Assertions.assertThat(matches).isEmpty();

		// Display the menu and click an entry
		css1("#button-quick-tasks").click();
		WebElement qtAdd = css1("#qt-add-album-to-series");
		waitClickable(qtAdd);
		qtAdd.click();

		// Set a title
		css1("#field_album_title").sendKeys("My new album for a previously empty series");

		waitForRepeatablePartInit();
		submitMainModelForm();

		assertThat(getWebDriver().getCurrentUrl()).startsWith("http://localhost/albums/view/");

		assertAlbumTitle("My new album for a previously empty series");
	}

	private void assertAlbumTitle(String title) {
		assertThat(css1("#album-identification :nth-child(1) .dem-form__value")).textContent()
				.isEqualToIgnoringWhitespace(title);
	}
}
