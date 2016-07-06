package org.demyo.web.test.mvc;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.demyo.test.AbstractPersistenceTest;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Base class for MVC integration tests.
 */
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/demyo-context.xml")
@WebAppConfiguration
public abstract class AbstractMvcTest extends AbstractPersistenceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMvcTest.class);

	@Autowired
	private WebApplicationContext wac;

	/** The HTMLUnit Web Client. */
	private WebClient webClient;

	/**
	 * Sets up the HTMLUnit Web Client.
	 * 
	 * @throws Exception in case of error during setup.
	 */
	@Before
	public void setupWebClient() throws Exception {
		webClient = MockMvcWebClientBuilder.webAppContextSetup(wac).build();
	}

	/**
	 * Gets the HTMLUnit Web Client.
	 * 
	 * @return the HTMLUnit Web Client
	 */
	protected WebClient getWebClient() {
		return webClient;
	}

	/**
	 * Returns the main model edit form on the page.
	 * 
	 * @param page The page to get the form from.
	 * @return The form, or <code>null</code> if no form was found.
	 */
	protected final HtmlForm getMainModelForm(HtmlPage page) {
		List<?> forms = page.getByXPath("//form[@class='dem-model-form']");
		if (forms.size() <= 0) {
			LOGGER.error("No form found in specified page: {}", page.getUrl().toString());
			return null;
		}

		Object form = forms.get(0);
		if (!(form instanceof HtmlForm)) {
			LOGGER.error("An element was found but is not a form in specified page: {}", page.getUrl().toString());
			return null;
		}
		return (HtmlForm) form;
	}

	/**
	 * Sets a text input on a page.
	 * 
	 * @param page The page.
	 * @param fieldId The field ID.
	 * @param value The value to set.
	 */
	protected final void setHtmlTextInput(HtmlPage page, String fieldId, String value) {
		page.<HtmlTextInput> getHtmlElementById(fieldId).setValueAttribute(value);
	}

	/**
	 * Sets a text area on a page.
	 * 
	 * @param page The page.
	 * @param fieldId The field ID.
	 * @param value The value to set.
	 */
	protected final void setHtmlTextArea(HtmlPage page, String fieldId, String value) {
		page.<HtmlTextArea> getHtmlElementById(fieldId).setText(value);
	}

	/**
	 * Submits a form.
	 * 
	 * @param form The form to submit.
	 * @return The page to which the form redirects.
	 * @throws IOException If an IO error occurs
	 */
	protected final HtmlPage submitForm(HtmlForm form) throws IOException {
		HtmlSubmitInput submit = form.<HtmlSubmitInput> getOneHtmlElementByAttribute("input", "type", "submit");
		return submit.click();
	}

	/**
	 * Clicks a link on a page.
	 * 
	 * @param page The page.
	 * @param id The link ID.
	 * @return The page to which the link redirects.
	 * @throws IOException If an IO error occurs
	 */
	protected HtmlPage clickLinkById(HtmlPage page, String id) throws IOException {
		return page.<HtmlAnchor> getHtmlElementById("qt-edit-author").click();
	}

	/**
	 * Deletes a model by using the delete URL given by the quick task.
	 * 
	 * @param page The view page for the model.
	 * @param id The ID of the delete quicktask.
	 * @return The page to which the action redirects.
	 * @throws IOException If an IO error occurs
	 */
	protected HtmlPage deleteWithQuickTask(HtmlPage page, String id) throws IOException {
		String deleteHref = page.<HtmlAnchor> getHtmlElementById(id).getHrefAttribute();
		String fullDeleteUrl = page.getUrl().toString() + "/../" + deleteHref;
		return getWebClient().getPage(new WebRequest(new URL(fullDeleteUrl), HttpMethod.POST));
	}
}
