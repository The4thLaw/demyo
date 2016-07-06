package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlNav;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Generic integration tests for the menu.
 */
public class MenuIT extends AbstractMvcTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MenuIT.class);

	/**
	 * Tests that all menu entries point to an existing page.
	 * 
	 * @throws IOException In case of I/O error.
	 */
	@Test
	public void testEntries() throws IOException {
		HtmlPage page = getWebClient().getPage("http://localhost/");

		HtmlNav menu = page.<HtmlNav> getHtmlElementById("main-menu");
		DomNodeList<HtmlElement> anchors = menu.getElementsByTagName("a");
		for (HtmlElement anchor : anchors) {
			LOGGER.warn("Testing {}", ((HtmlAnchor) anchor).getHrefAttribute());
			HtmlPage newPage = (HtmlPage) anchor.click();
			assertThat(newPage.getWebResponse().getStatusCode()).isEqualTo(HttpStatus.OK.value());

		}
	}
}
