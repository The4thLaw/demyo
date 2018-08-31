package org.demyo.web.test.mvc;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
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
import com.github.springtestdbunit.DbUnitTestExecutionListener;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.service.IConfigurationService;
import org.demyo.test.AbstractPersistenceTest;

/**
 * Base class for MVC integration tests.
 * 
 * <p>
 * Both WebClient and WebDriver are accepted depending on the scope of the actions. WebClient provides low-level access
 * to some details (e.g. HTTP statuses) while WebDriver provides powerful CSS selectors.
 * </p>
 */
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/demyo-context.xml")
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public abstract class AbstractMvcTest extends AbstractPersistenceTest {
	private static final int MAX_TIMEOUT = 1;

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMvcTest.class);

	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private IConfigurationService config;

	/** The HTMLUnit Web Client. */
	private WebClient webClient;
	/** The Selenium Web Driver. */
	private WebDriver webDriver;

	/**
	 * Sets flags specific for ITs.
	 * <ul>
	 * <li>Async loading for LESS</li>
	 * <li>Headless mode</li>
	 * </ul>
	 */
	@BeforeClass
	public static void setITFlags() {
		System.setProperty("java.awt.headless", "true");
		// Required for IT's since Less 2.7.2: else the body is hidden
		SystemConfiguration.getInstance().setLoadLessInAsync(true);
	}

	/**
	 * Sets a common base (English) for labels in views.
	 */
	@Before
	public void setLanguage() {
		// Have a common base for labels
		config.getConfiguration().setLanguage(Locale.ENGLISH);
	}

	/**
	 * Sets up the HTMLUnit Web Client.
	 * 
	 * @throws Exception in case of error during setup.
	 */
	@Before
	public void setupWebClient() throws Exception {
		webClient = MockMvcWebClientBuilder.webAppContextSetup(wac).build();
		webDriver = MockMvcHtmlUnitDriverBuilder.webAppContextSetup(wac).build();
	}

	/**
	 * Sets a reader in the current Web driver.
	 * <p>
	 * This is required so that the layout does not crash due to an uninitialized reader context.
	 * </p>
	 */
	@Before
	public void setCurrentReader() {
		// We must set a reader to set the relevant cookies and avoid issues with a missing reader context
		getWebDriver().get("http://localhost/readers/2/select");
	}

	/**
	 * Clears all caches.
	 */
	@Before
	public void clearCaches() {
		// We must clear all caches for each test case, else the reloads by
		// DBUnit break the tests
		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
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
	 * Gets the Selenium Web Driver.
	 * 
	 * @return the Selenium Web Driver
	 */
	protected WebDriver getWebDriver() {
		return webDriver;
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
		page.<HtmlTextInput>getHtmlElementById(fieldId).setValueAttribute(value);
	}

	/**
	 * Sets a text area on a page.
	 * 
	 * @param page The page.
	 * @param fieldId The field ID.
	 * @param value The value to set.
	 */
	protected final void setHtmlTextArea(HtmlPage page, String fieldId, String value) {
		page.<HtmlTextArea>getHtmlElementById(fieldId).setText(value);
	}

	/**
	 * Submits a form.
	 * 
	 * @param form The form to submit.
	 * @return The page to which the form redirects.
	 * @throws IOException If an IO error occurs
	 */
	protected final HtmlPage submitForm(HtmlForm form) throws IOException {
		HtmlSubmitInput submit = form.<HtmlSubmitInput>getOneHtmlElementByAttribute("input", "type", "submit");
		return submit.click();
	}

	/**
	 * Submits the main model edit form on the page.
	 */
	protected final void submitMainModelForm() {
		css1("form.dem-model-form input[type='submit']").click();
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
		return page.<HtmlAnchor>getHtmlElementById("qt-edit-author").click();
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
		String deleteHref = page.<HtmlAnchor>getHtmlElementById(id).getHrefAttribute();
		String fullDeleteUrl = page.getUrl().toString() + "/../" + deleteHref;
		return getWebClient().getPage(new WebRequest(new URL(fullDeleteUrl), HttpMethod.POST));
	}

	/**
	 * Uses the WebDriver to fetch one element matching a particular CSS selector (the first one).
	 * 
	 * @param selector The CSS selector to use.
	 * @return The matching element.
	 */
	protected WebElement css1(String selector) {
		return getWebDriver().findElement(By.cssSelector(selector));
	}

	/**
	 * Uses the WebDriver to fetch multiple elements matching a particular CSS selector.
	 * 
	 * @param selector The CSS selector to use.
	 * @return The matching elements.
	 */
	protected List<WebElement> cssM(String selector) {
		return getWebDriver().findElements(By.cssSelector(selector));
	}

	/**
	 * Sets the value of a specific by changing its DOM value attribute. Workaround when
	 * {@link WebElement#sendKeys(CharSequence...)} does not work.
	 * 
	 * @param fieldId The field ID.
	 * @param value The escaped value.
	 */
	protected void setFieldValue(String fieldId, String value) {
		getJavaScriptExecutor().executeScript("document.getElementById('" + fieldId + "').value='" + value + "'");
	}

	/**
	 * Gets the {@link JavascriptExecutor} if the current {@link WebDriver} supports it. Fails if it doesn't.
	 * 
	 * @return the {@link JavascriptExecutor}.
	 */
	protected JavascriptExecutor getJavaScriptExecutor() {
		if (!(webDriver instanceof JavascriptExecutor)) {
			throw new RuntimeException("The WebDriver " + webDriver + " does not support JavaScript execution");
		}
		return (JavascriptExecutor) webDriver;
	}

	/**
	 * Waits for an element to be clickable.
	 * 
	 * @param element The element to wait for.
	 */
	protected void waitClickable(WebElement element) {
		LOGGER.debug("Waiting for element to become clickable: {}", element);
		new WebDriverWait(webDriver, MAX_TIMEOUT).until(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * Wait for a long amount of time.
	 */
	protected void waitLong() {
		try {
			Thread.sleep(750L);
		} catch (InterruptedException e) {
			LOGGER.warn("Sleep interrupted", e);
		}
	}
}
