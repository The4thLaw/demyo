package org.demyo.web.velocity.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.ValidScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * Velocity tool for paging and text list management.
 */
@ValidScope(value = Scope.APPLICATION)
public class PagingTool {
	private static final Logger LOGGER = LoggerFactory.getLogger(PagingTool.class);

	/** The number of pages to always display at the start and end of the list of page links. */
	private static final int PAGES_COUNT_BOUNDS = 2;
	/** The number of pages to display on both sides of the current page. */
	private static final int PAGES_COUNT_AROUND = 3;

	/**
	 * Default constructor.
	 */
	public PagingTool() {
		LOGGER.debug("Creating instance");
	}

	/**
	 * Returns the first letter of the given string, stripped of its accents or other diacritics. If the first character
	 * is not a letter, returns '#'.
	 * 
	 * @param s The string to parse.
	 * @return The first letter, or substitute character.
	 */
	public char getFirstLetter(String s) {
		// See http://stackoverflow.com/questions/5697171/regex-what-is-incombiningdiacriticalmarks
		// Note that this removes diacritics, but special characters won't be transliterated. For example 'Đ' won't
		// become 'D'. But it seems OK atm
		String normalized = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{Mn}+", "");
		char c = normalized.charAt(0);
		if (!Character.isLetter(c)) {
			return '#';
		}
		return Character.toUpperCase(c);
	}

	/**
	 * Gets the list of links to individual pages. This method forwards parameters according to
	 * {@link #getBaseUrlForPageLinks(HttpServletRequest)}.
	 * 
	 * @param list The paginated list of entities.
	 * @param request The HTTP request.
	 * @param jsTool The {@link JavascriptTool} in this request.
	 * @return The page links (as an HTML fragment).
	 * @throws UnsupportedEncodingException If encoding the parameters fails.
	 */
	public String pageLinks(Page<?> list, HttpServletRequest request, JavascriptTool jsTool)
			throws UnsupportedEncodingException {
		return pageLinks((Slice<?>) list, request, jsTool);
	}

	/**
	 * Gets the pagination links. This method forwards parameters according to
	 * {@link #getBaseUrlForPageLinks(HttpServletRequest)}.
	 * 
	 * @param slice The paginated list of entities.
	 * @param request The HTTP request.
	 * @param jsTool The {@link JavascriptTool} in this request.
	 * @return The page links (as an HTML fragment).
	 * @throws UnsupportedEncodingException If encoding the parameters fails.
	 */
	// TODO [TBD]: allow to generate letters on the fly or not (boolean parameter)
	public String pageLinks(Slice<?> slice, HttpServletRequest request, JavascriptTool jsTool)
			throws UnsupportedEncodingException {
		int current = slice.getNumber() + 1; // Slices starts at 0
		LOGGER.debug("Generating page links for slice {}", current);

		StringBuilder baseUrl = getBaseUrlForPageLinks(request);
		baseUrl.append("page=");

		final String previousLink;
		if (slice.hasPrevious()) {
			previousLink = "<a class='mdl-button mdl-js-button mdl-js-ripple-effect' id='page-link-prev' href='"
					+ baseUrl.toString() + (current - 1)
					+ "' data-dem-shortcut='37'><span class='dico'>arrow_left</span></a> ";
		} else {
			previousLink = "<a class='mdl-button mdl-js-button mdl-button--disabled' href='#'>"
					+ "<span class='dico'>arrow_left</span></a> ";
		}

		final String nextLink;
		if (slice.hasNext()) {
			nextLink = " <a class='mdl-button mdl-js-button mdl-js-ripple-effect' id='page-link-next' href='"
					+ baseUrl.toString() + (current + 1)
					+ "' data-dem-shortcut='39'><span class='dico'>arrow_right</span></a>";
		} else {
			nextLink = " <a class='mdl-button mdl-js-button mdl-button--disabled' href='#'>"
					+ "<span class='dico'>arrow_right</span></a>";
		}

		return "<div id='dem-paging'>" + previousLink + nextLink + "</div>";
	}

	/**
	 * Gets the base URL to make direct links to pages. This method takes the convention of forwarding all parameters
	 * starting with "with" or "from".
	 * 
	 * @param request The HTTP request.
	 * @return The base URL, ready to support an additional "page" parameter, for example. The URL will always end with
	 *         a "?" or "&".
	 * @throws UnsupportedEncodingException If encoding the parameters fails.
	 */
	private StringBuilder getBaseUrlForPageLinks(HttpServletRequest request) throws UnsupportedEncodingException {
		StringBuilder baseUrl = new StringBuilder(request.getRequestURI());
		baseUrl.append('?');
		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			String param = entry.getKey();
			if (param.startsWith("from") || param.startsWith("with")) {
				baseUrl.append(param).append('=').append(URLEncoder.encode(entry.getValue()[0], "UTF-8"))
						.append("&amp;");
			}
		}
		return baseUrl;
	}

	/**
	 * Gets the numbers of the pages to link to. Uses a moving window of {@link #PAGES_COUNT_AROUND} pages around the
	 * current page, and always displays {@link #PAGES_COUNT_BOUNDS} pages at the start of the list.
	 * 
	 * @param current The number of the current page.
	 * @param max The maximum number of pages.
	 * @return The list of page numbers.
	 */
	protected List<Integer> getPageNumbers(int current, int max) {
		List<Integer> pages = new ArrayList<Integer>();

		// Pages up to the current one
		int p = 1;
		// Pages at start
		for (int i = 0; i < PAGES_COUNT_BOUNDS && i + p < current; i++) {
			pages.add(p++);
		}
		// Pages before the current one
		for (p = (p > current - PAGES_COUNT_AROUND) ? p : current - PAGES_COUNT_AROUND; p < current; p++) {
			pages.add(p);
		}

		// Current
		assert (p == current);
		pages.add(p++);

		// Pages after the current one
		for (int i = 0; i < PAGES_COUNT_AROUND && p + i < max; i++) {
			pages.add(p++);
		}
		// Pages at end
		for (p = (p > max - PAGES_COUNT_BOUNDS) ? p : max - PAGES_COUNT_BOUNDS; p <= max; p++) {
			pages.add(p);
		}

		return pages;
	}

	/**
	 * Displays a set of letters to jump directly to an entry starting with that letter.
	 * 
	 * @param request The HTTP request.
	 * @return The HTML for the jump list.
	 * @throws UnsupportedEncodingException If encoding forwarded parameters fails.
	 */
	public String letters(HttpServletRequest request) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder("<div class=\"pages_nav_letters\">");
		StringBuilder baseUrl = getBaseUrlForPageLinks(request);
		for (char c = 'a'; c <= 'z'; c++) {
			sb.append("\n\t<a href=\"").append(baseUrl).append("startsWith=").append(c).append("\">").append(c)
					.append("</a>");
		}
		sb.append("\n</div>");
		return sb.toString();
	}
}
