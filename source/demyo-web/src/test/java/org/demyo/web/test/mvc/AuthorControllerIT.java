package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.demyo.web.controller.AuthorController;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Integration tests for {@link AuthorController}.
 */
// TODO: migrate to WebDriver like AlbumControllerIT to have a single way of doing things?
public class AuthorControllerIT extends AbstractMvcTest {
	/**
	 * Tests an add/edit/delete sequence.
	 * 
	 * @throws IOException in case of IO error.
	 */
	@Test
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

		// TODO: this should return a 404. Currently returns a 500.
		//getWebClient().getPage(viewUrl);
	}
}
