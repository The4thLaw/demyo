package org.demyo.web.interceptors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import org.demyo.model.Reader;
import org.demyo.service.IReaderContext;
import org.demyo.service.IReaderService;

/**
 * A {@link HandlerInterceptor} that sets the reader in the {@link IReaderContext} if possible.
 * <p>
 * If not, redirects to the selection URL.
 * </p>
 */
public class ReaderInterceptor implements HandlerInterceptor {
	private static final int COOKIE_EXPIRATION = 365 * 24 * 60 * 60;
	private static final String READER_COOKIE = "demyo_reader_id";
	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderInterceptor.class);

	@Autowired
	private IReaderService service;
	@Autowired
	private IReaderContext context;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		final String path = request.getServletPath();
		LOGGER.debug("Handling {}", path);

		boolean hasReader = false;

		// Special behaviour to avoid making a controll
		if (path.matches("^/readers/select/(\\d+)$")) {
			Long readerId = parseReaderId(path.replaceAll("^/readers/select/", ""));
			LOGGER.debug("Selecting reader {}", readerId);
			hasReader = loadReaderIfExists(readerId, request, response);
			// The controller will take care of the redirection
		}

		// Load from cookie if possible
		if (!hasReader) {
			Cookie cookie = WebUtils.getCookie(request, READER_COOKIE);
			String cookieValue = cookie == null ? null : cookie.getValue();
			Long readerId = parseReaderId(cookieValue);

			// If the reader is selected and exists. Also refreshes the cookie
			hasReader = loadReaderIfExists(readerId, request, response);
		}

		// If the user is selected properly, continue
		if (hasReader) {
			LOGGER.debug("The current reader is: {}", context.getCurrentReader().getName());
			return true;
		}

		// Clear the current reader already
		context.clearCurrentReader();

		// Check how many readers are in total
		Reader uniqueReader = service.getUniqueReader();
		if (uniqueReader != null) {
			LOGGER.debug("Loaded the unique available reader");
			// We have a unique reader we can use. Set it, and set the cookie
			setReader(request, response, uniqueReader);
		} else {
			// We have multiple readers, the user should select by himself, except if we are already on the right page
			if (!path.matches("^/readers(/(index)?)?")) {
				response.sendRedirect(request.getContextPath() + "/readers/");
			}
		}

		return true;
	}

	private boolean loadReaderIfExists(Long readerId, HttpServletRequest request, HttpServletResponse response) {
		if (readerId != null && service.readerExists(readerId)) {
			// He does. Perhaps we already loaded the reader
			Reader currentReader = context.getCurrentReader();
			if (currentReader != null && currentReader.getId().equals(readerId)) {
				// Do nothing
				LOGGER.debug("Reader {} is already loaded in the session", readerId);
			} else {
				loadReader(readerId, request, response);
			}
			return true;
		}
		return false;
	}

	private Long parseReaderId(String tentativeId) {
		Long readerId = null;
		if (tentativeId != null) {
			try {
				readerId = Long.parseLong(tentativeId);
			} catch (RuntimeException e) {
				LOGGER.warn("Invalid reader ID: {}", tentativeId);
			}
		}
		return readerId;
	}

	/**
	 * Sets the reader in the context and cookie.
	 */
	private void setReader(HttpServletRequest request, HttpServletResponse response, Reader reader) {
		Cookie cookieToSet = new Cookie(READER_COOKIE, reader.getId().toString());
		cookieToSet.setMaxAge(COOKIE_EXPIRATION);
		cookieToSet.setPath(request.getContextPath() + "/");
		response.addCookie(cookieToSet);
		context.setCurrentReader(reader);
	}

	/**
	 * Loads the reader and sets it in the context.
	 */
	private void loadReader(Long readerId, HttpServletRequest request, HttpServletResponse response) {
		LOGGER.debug("Loading reader {}", readerId);
		Reader reader = service.getByIdForView(readerId);
		context.setCurrentReader(reader);
		setReader(request, response, reader);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// Nothing to do
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// Nothing to do
	}

}
