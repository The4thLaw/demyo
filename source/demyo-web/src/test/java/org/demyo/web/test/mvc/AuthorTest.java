package org.demyo.web.test.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

// TODO: refactor code to abstract class
// TODO: due to overhead, execute in integration, rather than unit tests
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/demyo-context.xml")
@WebAppConfiguration
public class AuthorTest {
	@Autowired
	private WebApplicationContext wac;

	private WebClient webClient;

	@BeforeClass
	public static void setupDataSource() throws NamingException {
		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		DataSource ds = new DriverManagerDataSource("jdbc:h2:/tmp/demyo_tests;DB_CLOSE_DELAY=120;IGNORECASE=TRUE");
		builder.bind("jdbc/demyoDataSource", ds);
		// TODO: determine how to reset
	}

	@Before
	public void setupWebClient() throws Exception {
		webClient = MockMvcWebClientBuilder.webAppContextSetup(wac).build();
	}

	@Test
	public void testIndex() throws Exception {
		HtmlPage page = webClient.getPage("http://localhost/authors/add");
		HtmlForm form = getMainModelForm(page);
		//System.err.println(form.asXml());
		page.<HtmlTextInput> getHtmlElementById("field_author_name").setValueAttribute("Buchet");
		HtmlSubmitInput submit = form.<HtmlSubmitInput> getOneHtmlElementByAttribute("input", "type", "submit");
		HtmlPage newMessagePage = submit.click();
		// TODO: make useful assertions
		assertThat(newMessagePage.getUrl().toString()).endsWith("/authors/view/1");
		// TODO: above will break if DB is persisted from test to test (which it shouldn't, but still...)
	}

	public final HtmlForm getMainModelForm(HtmlPage page) {
		// TODO: catch errors and return null if there is not form. Log the reason
		List<?> forms = page.getByXPath("//form[@class='dem-model-form']");
		return (HtmlForm) forms.get(0);
	}
}
