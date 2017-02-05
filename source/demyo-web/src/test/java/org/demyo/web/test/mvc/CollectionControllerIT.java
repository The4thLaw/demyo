package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.demyo.web.controller.CollectionController;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * Integration tests for {@link CollectionController}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class CollectionControllerIT extends AbstractMvcTest {

	/**
	 * Tests an add/edit/delete sequence.
	 * 
	 * @throws IOException in case of IO error.
	 */
	@Test
	public void testDelete() throws IOException {
		String viewUrl = "http://localhost/collections/view/1";
		HtmlPage page = getWebClient().getPage(viewUrl);

		// Delete
		page = deleteWithQuickTask(page, "qt-delete-collection");

		// Check redirect after delete. Must be to the publisher index, as there is no collection indec
		assertThat(page.getUrl().toString()).matches(".*/publishers/$");

		try {
			getWebClient().getPage(viewUrl);
			Assertions.fail("Should have gotten a 404 error");
		} catch (FailingHttpStatusCodeException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}
