package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import org.demyo.web.controller.DerivativeController;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Integration tests for {@link DerivativeController}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class DerivativeControllerIT extends AbstractMvcTest {

	/**
	 * Tests adding a Derivative without Series.
	 */
	@Test
	public void testAddPageNoSeries() {
		getWebDriver().get("http://localhost/derivatives/add");

		// We need to add the option manually: interacting with Chosen here is a pain and the callbacks for the
		// dependent select don't fire
		// Still better than nothing: we make sure that the controller doesn't reject Derivatives with empty Series 
		getJavaScriptExecutor().executeScript(
				"$('#field_derivative_album_id').append('<option value=\"2\">2</option>')");
		// Set the Album to a one shot
		setFieldValue("field_derivative_album_id", "2");

		submitMainModelForm();

		assertThat(getWebDriver().getCurrentUrl()).startsWith("http://localhost/derivatives/view/");
	}
}
