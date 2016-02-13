package org.demyo.web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.IDemyoException;
import org.demyo.service.IConfigurationService;
import org.demyo.service.ITranslationService;
import org.demyo.utils.io.DIOUtils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

/**
 * Base controller for Demyo.
 */
public abstract class AbstractController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);
	private static final String MODEL_KEY_LAYOUT = "layout";
	private static final String LAYOUT_AJAX = "layout/ajax.vm";

	@Autowired
	private IConfigurationService configService;
	@Autowired
	private ITranslationService translationService;

	private MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();

	protected AbstractController() {
		// May be missing from some systems at least
		mimeTypes.addMimeTypes("image/png png");

		// Note: intentionally, there is no specific MIME type for DEA export files.
		// The HTTP spec discourages vendor-specific MIME types
	}

	@ExceptionHandler
	private ModelAndView demyoExceptionHandler(Exception ex, HttpServletResponse response) throws Exception {
		boolean statusSet = false;
		if (ex instanceof IDemyoException) {
			IDemyoException ide = (IDemyoException) ex;
			if (ide.is(DemyoErrorCode.IMAGE_NOT_FOUND)) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				statusSet = true;
			}
		}
		if (!statusSet) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		LOGGER.error("Uncaught error reached the exception handler", ex);
		ModelAndView model = new ModelAndView("core/exception");
		model.addObject("exception", ex);
		model.addObject("demyoTranslationService", translationService);
		model.addObject("appConfig", configService.getConfiguration());
		return model;
	}

	/**
	 * Sets the layout to an ajax-compatible format.
	 * 
	 * @param model The model to set the layout in.
	 */
	protected void setLayoutAjax(Model model) {
		model.addAttribute(MODEL_KEY_LAYOUT, LAYOUT_AJAX);
	}

	/**
	 * Sets the translation service into the model.
	 * 
	 * @param model The view model.
	 */
	@ModelAttribute
	private void initTranslationService(Model model) {
		model.addAttribute("demyoTranslationService", translationService);
	}

	/**
	 * Sets the application configuration into the model.
	 * 
	 * @param model The view model.
	 */
	@ModelAttribute
	private void initConfiguration(Model model) {
		model.addAttribute("appConfig", configService.getConfiguration());
	}

	/**
	 * Sends an HTTP redirect.
	 * 
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @param relativeUrl An URL, relative to the context, to redirect to.
	 * @return Always <code>null</code>.
	 * @deprecated Use {@link #redirect(String)} instead.
	 */
	@Deprecated
	protected String redirect(HttpServletRequest request, HttpServletResponse response, String relativeUrl) {
		try {
			response.sendRedirect(request.getContextPath() + relativeUrl);
		} catch (IOException e) {
			throw new RuntimeException("Failed to send redirect", e);
		}
		return null;
	}

	/**
	 * Sends an HTTP redirect.
	 * 
	 * @param relativeUrl An URL, relative to the context, to redirect to.
	 * @return The redirection URL.
	 */
	protected String redirect(String relativeUrl) {
		// Note: spring is aware of the context path
		return "redirect:" + relativeUrl;
	}

	/**
	 * Checks the "If-Modified-Since" header and honours it.
	 * <p>
	 * If the header is set and is higher or equal to the reference date, sends a "Not Modified" HTTP response and
	 * returns <code>true</code>.
	 * </p>
	 * 
	 * @param reference The reference timestamp.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @return <code>true</code> if the method sent a "Not Modified" HTTP response.
	 */
	protected boolean handleLastModified(long reference, HttpServletRequest request, HttpServletResponse response) {
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if (ifModifiedSince >= 0 && ifModifiedSince >= reference) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return true;
		}
		return false;
	}

	/**
	 * Sends a local file for download by the client. Honours the "If-Modified-Since" header, and guesses the
	 * content-type.
	 * 
	 * @param file The file to download.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws IOException In case of error while sending the file to the client.
	 */
	protected void download(File file, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		download(file, null, request, response);
	}

	/**
	 * Sends a local file for download by the client. Honours the "If-Modified-Since" header, and guesses the
	 * content-type.
	 * 
	 * @param file The file to download.
	 * @param filename An file name to advertise. If <code>null</code>, rely on browser defaults.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws IOException In case of error while sending the file to the client.
	 */
	protected void download(File file, String filename, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (!file.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			LOGGER.warn("File {} was not found, returning a 404", file);
			return;
		}
		if (handleLastModified(file.lastModified(), request, response)) {
			return;
		}

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		response.setDateHeader("Last-Modified", file.lastModified());
		response.setContentLength((int) file.length());
		response.setContentType(mimeTypes.getContentType(file));

		if (filename != null) {
			String targetFilename = URLEncoder.encode(filename, "UTF-8");
			targetFilename = URLDecoder.decode(targetFilename, "ISO8859_1");
			response.setHeader("Content-disposition", "attachment; filename=" + targetFilename);
		}

		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			IOUtils.copy(bis, response.getOutputStream());
			response.flushBuffer();
		} finally {
			DIOUtils.closeQuietly(fis);
			DIOUtils.closeQuietly(bis);
		}
	}
}
