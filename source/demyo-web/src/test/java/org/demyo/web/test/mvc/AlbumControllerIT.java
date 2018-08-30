package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.demyo.web.test.mvc.WebDriverAssertions.assertThat;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.model.Album;
import org.demyo.model.Reader;
import org.demyo.model.Series;
import org.demyo.service.IReaderContext;
import org.demyo.service.IReaderService;
import org.demyo.web.controller.AlbumController;

/**
 * Integration tests for {@link AlbumController}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class AlbumControllerIT extends AbstractMvcTest {
	@Component
	@Primary
	public static class MockReaderContext implements IReaderContext {
		private Reader reader;

		@Override
		public Reader getCurrentReader() {
			return reader;
		}

		@Override
		public void setCurrentReader(Reader r) {
			this.reader = r;
		}

		@Override
		public void clearCurrentReader() {
			// Do nothing, preserve whatever was set by the unit tests
		}

		@Override
		public boolean isFavouriteSeries(Series s) {
			return false;
		}

		@Override
		public boolean isFavouriteAlbum(Album a) {
			return false;
		}

		@Override
		public boolean isAlbumInReadingList(Album a) {
			return false;
		}

	}

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
		assertThat(titles.get(i++)).textContent().isEqualToIgnoringWhitespace("Bludzee");
		assertThat(titles.get(i++)).textContent().isEqualToIgnoringWhitespace(NEW_ALBUM_TITLE);
		assertThat(titles.get(i++)).textContent().isEqualToIgnoringWhitespace("XIII");
		assertThat(titles.get(i++)).textContent().isEqualToIgnoringWhitespace("Z_test");
	}

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

	@Autowired
	// private IReaderContext readerContext;
	private IReaderService readerService;

	/**
	 * Tests adding an Album.
	 */
	@Test
	public void testAddPage() {

		// TODO: put this in the init of the MVC controller.
		// Check if we can override the starting domain to be able to set the cookie without accessing the home page
		// first. Unlikely since HtmlUnit relies on lastPage() to get the domain.
		// Maybe check with a newer version of Selenium / HtmlUnit, else open a bug report
		Reader mockReader = new Reader();
		mockReader.setId(-1L);
		mockReader.setName("Mock Reader");
		readerService.getContext().setCurrentReader(mockReader);

		getWebDriver().get("http://localhost/");

		// getWebDriver().manage().addCookie(new Cookie("demyo_reader_id", "2"));
		getWebDriver().manage().addCookie(new Cookie.Builder("demyo_reader_id", "2").domain("localhost")
				.isHttpOnly(false).isSecure(false).path("/").build());

		getWebDriver().get("http://localhost/albums/add");

		// Set a title
		System.err.println(getWebDriver().getPageSource());
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
	 * Tests editing an album.
	 * 
	 * @throws InterruptedException In case the thread waiting for JavaScript execution is interrupted.
	 */
	@Test
	public void testEditPage() throws InterruptedException {
		getWebDriver().get("http://localhost/albums/edit/1");

		// Wait for JavaScript to execute
		waitLong();

		// For some reason we need to call this manually, else the template is
		// left behind
		// getJavaScriptExecutor().executeScript("demyo.bindRepeatableParts()");

		assertThat(css1("#field_album_priceList\\[0\\]_date")).hasAttributeEqualTo("value", "2016-01-10");
		assertThat(css1("#field_album_priceList\\[0\\]_price")).hasAttributeEqualTo("value", "10.0");
		assertThat(css1("#field_album_priceList\\[1\\]_date")).hasAttributeEqualTo("value", "2016-01-15");
		assertThat(css1("#field_album_priceList\\[1\\]_price")).hasAttributeEqualTo("value", "1.5");

		submitMainModelForm();

		assertThat(getWebDriver().getCurrentUrl()).isEqualTo("http://localhost/albums/view/1");

		// Check the Album name
		assertAlbumTitle("3 - Toutes les larmes de l'enfer");
	}

	/**
	 * Tests that it is possible to add an album to an empty Series.
	 * 
	 * @throws InterruptedException In case the thread waiting for JavaScript execution is interrupted.
	 * 
	 * @see commit b01c5e7240866ee82ccb3dd6928f0fc245381754
	 */
	@Test
	public void testAddToEmptySeries() throws InterruptedException {
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

		// Wait for JavaScript to execute. Could be smarter and instead check that element has indeed disappeared
		waitLong();

		submitMainModelForm();

		assertThat(getWebDriver().getCurrentUrl()).startsWith("http://localhost/albums/view/");

		assertAlbumTitle("My new album for a previously empty series");
	}

	private void assertAlbumTitle(String title) {
		assertThat(css1("#album-identification :nth-child(1) .dem-form__value")).textContent()
				.isEqualToIgnoringWhitespace(title);
	}
}
