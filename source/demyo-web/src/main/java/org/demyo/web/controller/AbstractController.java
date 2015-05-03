package org.demyo.web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.demyo.model.exception.DemyoErrorCode;
import org.demyo.model.exception.IDemyoException;
import org.demyo.service.ITranslationService;
import org.demyo.utils.io.DIOUtils;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Base controller for Demyo.
 * 
 * @author Xavier 'Xr' Dalem
 * @version $Revision$
 */
public abstract class AbstractController {
	private static final String MODEL_KEY_LAYOUT = "layout";
	private static final String LAYOUT_AJAX = "layout/ajax.vm";

	@Autowired
	private ITranslationService translationService;

	private MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();

	@ExceptionHandler
	private void demyoExceptionHandler(Exception ex, HttpServletResponse response) throws Exception {
		if (ex instanceof IDemyoException) {
			IDemyoException ide = (IDemyoException) ex;
			if (ide.is(DemyoErrorCode.IMAGE_NOT_FOUND)) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}
		throw ex; // TODO: send to a proper view
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
		if (handleLastModified(file.lastModified(), request, response)) {
			return;
		}

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		response.setDateHeader("Last-Modified", file.lastModified());
		response.setContentLength((int) file.length());
		response.setContentType(mimeTypes.getContentType(file));
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
