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
import org.demyo.service.IReaderService;

public class ReaderInterceptor implements HandlerInterceptor {
	private static final String READER_COOKIE = "demyo_reader_id";
	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderInterceptor.class);

	@Autowired
	private IReaderService service;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO: when setting the reader, don't use the cookie as reference, and allow the request to complete normally.
		// The controller will set the cookie
		LOGGER.debug("Handling {}", request.getRequestURI());
		LOGGER.debug("Handling {}", request.getServletPath());

		boolean hasReader = false;

		Cookie cookie = WebUtils.getCookie(request, READER_COOKIE);
		String cookieValue = cookie == null ? null : cookie.getValue();
		Long readerId = null;
		if (cookieValue != null) {
			try {
				readerId = Long.parseLong(cookieValue);
			} catch (RuntimeException e) {
				// Do nothing
			}
		}

		if (readerId != null) {
			// TODO: get whole tree, including favorites and reading list (?)
			// TODO: the results should be cached in L2
			Reader reader = service.getByIdForEdition(readerId);
			service.setCurrentReader(reader);
			hasReader = true;
		}

		// If the user is selected properly, continue
		if (hasReader) {
			return true;
		}

		// Check how many readers are in total
		// Reader uniqueReader = service.getUniqueReader();
		// if (uniqueReader != null) {
		// We have a unique reader we can use. Set it, and set the cookie
		// Cookie cookieToSet = new Cookie(READER_COOKIE, uniqueReader.getId());
		// response.addCookie(cookieToSet);
		// } else {
		// We have multiple readers, the user should select by himself
		// response.sendRedirect(location);
		// }

		return true;
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
