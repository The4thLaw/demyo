package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import org.demyo.web.controller.SeriesController;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Integration tests for {@link SeriesController}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class SeriesControllerIT extends AbstractMvcTest {
	private static final String ONGOING_SERIES_LABEL = "This series is ongoing.";
	private static final String COMLETED_SERIES_LABEL = "This series is over.";

	/**
	 * Tests that the location does not impact the completion status.
	 * 
	 * @see commit c06e438d368557285e7452a4d732d3c25e40a213
	 */
	@Test
	public void testCompletionAndLocation() {
		final String url = "http://localhost/series/view/2";
		final String sampleLocation = "My location";

		getWebDriver().get(url);
		assertThat(getWebDriver().getPageSource()).contains(ONGOING_SERIES_LABEL);
		assertThat(getWebDriver().getPageSource()).doesNotContain(COMLETED_SERIES_LABEL);

		getWebDriver().get("http://localhost/series/edit/2");
		css1("#field_series_location").sendKeys(sampleLocation);
		submitMainModelForm();
		assertThat(getWebDriver().getCurrentUrl()).isEqualTo(url);
		assertThat(getWebDriver().getPageSource()).contains(sampleLocation);
		assertThat(getWebDriver().getPageSource()).contains(ONGOING_SERIES_LABEL);
		assertThat(getWebDriver().getPageSource()).doesNotContain(COMLETED_SERIES_LABEL);

		getWebDriver().get("http://localhost/series/edit/2");
		css1("label[for=field_series_completed]").click();
		submitMainModelForm();
		assertThat(getWebDriver().getPageSource()).contains(sampleLocation);
		assertThat(getWebDriver().getPageSource()).contains(COMLETED_SERIES_LABEL);
		assertThat(getWebDriver().getPageSource()).doesNotContain(ONGOING_SERIES_LABEL);

		getWebDriver().get("http://localhost/series/edit/2");
		setFieldValue("field_series_location", "");
		submitMainModelForm();
		assertThat(getWebDriver().getPageSource()).doesNotContain(sampleLocation);
		assertThat(getWebDriver().getPageSource()).contains(COMLETED_SERIES_LABEL);
		assertThat(getWebDriver().getPageSource()).doesNotContain(ONGOING_SERIES_LABEL);
	}
}
