package org.demyo.web.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ReaderInterceptor implements HandlerInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		LOGGER.debug("Handling {}", request.getRequestURI());
		LOGGER.debug("Handling {}", request.getServletPath());
		request.getCookies();
		// If cookie with reader id, load reader and put in session
		// If user does not exist, act like no cookie
		// If no cookie and nly one reader, use that reader
		// If no cookie and 2+ users, redirect to users list if not already on user list
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
