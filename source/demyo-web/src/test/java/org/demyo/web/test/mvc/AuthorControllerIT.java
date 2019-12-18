package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.demyo.web.controller.AuthorController;

/**
 * Integration tests for {@link AuthorController}.
 */
public class AuthorControllerIT extends AbstractMvcTest {
	/**
	 * Tests an add/edit/delete sequence.
	 * 
	 * @throws IOException in case of IO error.
	 */
	@Test
	@Ignore("Broken by the Vue.js switch, due to be phased out")
	public void testAll() throws IOException {
		HtmlPage page = getWebClient().getPage("http://localhost/authors/add");

		// Set some values and save
		HtmlForm form = getMainModelForm(page);
		setHtmlTextInput(page, "field_author_firstName", "Philippe");
		setHtmlTextInput(page, "field_author_nickname", "Test");
		setHtmlTextInput(page, "field_author_name", "Buchet");
		page = submitForm(form);

		// Check redirect after add
		String viewUrl = page.getUrl().toString();
		assertThat(viewUrl).matches(".*/authors/view/[0-9]+$");

		// Check fields after add
		String pageText = page.asText();
		assertThat(pageText).contains("Philippe 'Test' Buchet");

		// Edit
		page = clickLinkById(page, "qt-edit-author");

		// Change some values
		form = getMainModelForm(page);
		setHtmlTextInput(page, "field_author_website", "http://www.example.org/buchet");
		setHtmlTextInput(page, "field_author_firstName", "Phil");
		page = submitForm(form);

		// Check redirect after edit
		assertThat(page.getUrl().toString()).isEqualTo(viewUrl);

		// Check fields after edit
		pageText = page.asText();
		assertThat(pageText).contains("Phil 'Test' Buchet");
		assertThat(pageText).contains("http://www.example.org/buchet");

		// Delete
		page = deleteWithQuickTask(page, "qt-delete-author");

		// Check redirect after delete
		assertThat(page.getUrl().toString()).matches(".*/authors/$");

		try {
			getWebClient().getPage(viewUrl);
			Assertions.fail("Should have gotten a 404 error");
		} catch (FailingHttpStatusCodeException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
