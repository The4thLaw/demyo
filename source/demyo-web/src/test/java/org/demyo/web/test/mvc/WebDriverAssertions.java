package org.demyo.web.test.mvc;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.StringAssert;
import org.assertj.core.internal.Objects;
import org.openqa.selenium.WebElement;

/**
 * Assertions to use with Selenium's API.
 */
public final class WebDriverAssertions {
	/**
	 * Specific asserter for {@link WebElement}s.
	 */
	public static class WebElementAssert extends AbstractObjectAssert<WebElementAssert, WebElement> {
		private final Objects objects = Objects.instance();

		/**
		 * Creates an asserter.
		 * 
		 * @param actual The actual value.
		 */
		public WebElementAssert(WebElement actual) {
			super(actual, WebElementAssert.class);
		}

		/**
		 * Returns a {@link StringAssert} for the textual content of this element.
		 * 
		 * @return The assert.
		 */
		public StringAssert textContent() {
			return new StringAssert(actual.getText());
		}

		/**
		 * Asserts that the element has an attribute of a given value.
		 * 
		 * @param attributeName The attribute name.
		 * @param value The expected attribute value.
		 */
		public void hasAttributeEqualTo(String attributeName, String value) {
			objects.assertEqual(info, actual.getAttribute(attributeName), value);
		}
	}

	private WebDriverAssertions() {
		// Prevent instances
	}

	/**
	 * Creates a new instance of {@link WebElementAssert}.
	 * 
	 * @param actual The actual value.
	 * @return The created assertion object.
	 */
	public static WebElementAssert assertThat(WebElement actual) {
		return new WebElementAssert(actual);
	}
}
