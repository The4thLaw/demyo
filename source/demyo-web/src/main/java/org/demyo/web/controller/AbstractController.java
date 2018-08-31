package org.demyo.web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.IDemyoException;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IReaderService;
import org.demyo.service.ITranslationService;

/**
 * Base controller for Demyo.
 */
public abstract class AbstractController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);
	private static final String MODEL_KEY_LAYOUT = "layout";
	private static final String MODEL_KEY_CONFIG = "appConfig";
	private static final String MODEL_KEY_VERSION = "appVersion";
	private static final String MODEL_KEY_CODENAME = "appCodename";
	private static final String MODEL_KEY_I18N_SERV = "demyoTranslationService";
	private static final String MODEL_KEY_READER_SERV = "demyoReaderService";
	private static final String MODEL_KEY_ASYNC_LESS = "loadLessInAsync";
	private static final String MODEL_KEY_BODY_CLASSES = "bodyClasses";
	private static final String LAYOUT_PLAIN = "layout/plain.vm";
	private static final String LAYOUT_MINIMAL = "layout/minimal.vm";

	@Autowired
	private IConfigurationService configService;
	@Autowired
	private ITranslationService translationService;
	@Autowired
	private IReaderService readerService;

	private MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();

	/**
	 * Default constructor.
	 */
	protected AbstractController() {
		// May be missing from some systems at least
		mimeTypes.addMimeTypes("image/png png");
		mimeTypes.addMimeTypes("image/jpeg jpg");
		mimeTypes.addMimeTypes("image/jpeg jpeg");

		// Note: intentionally, there is no specific MIME type for DEA export files.
		// The HTTP spec discourages vendor-specific MIME types
	}

	@ExceptionHandler
	private ModelAndView demyoExceptionHandler(Exception ex, HttpServletResponse response) throws Exception {
		LOGGER.error("Uncaught error reached the exception handler", ex);

		if (ex instanceof IDemyoException) {
			IDemyoException ide = (IDemyoException) ex;
			if (ide.is(DemyoErrorCode.IMAGE_NOT_FOUND)) {
				return demyo404Handler(ex, response);
			}
		}

		return handleError(ex, response, "core/exception", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	private ModelAndView handleError(Exception ex, HttpServletResponse response, String viewName, int status) {
		response.setStatus(status);
		ModelAndView model = new ModelAndView(viewName);
		model.addObject("exception", ex);
		model.addObject(MODEL_KEY_I18N_SERV, translationService);
		model.addObject(MODEL_KEY_CONFIG, configService.getConfiguration());
		model.addObject(MODEL_KEY_VERSION, SystemConfiguration.getInstance().getVersion());
		model.addObject(MODEL_KEY_CODENAME, SystemConfiguration.getInstance().getCodename());
		model.addObject(MODEL_KEY_READER_SERV, readerService);
		return model;
	}

	@ExceptionHandler(value = EntityNotFoundException.class)
	@GetMapping("/errors/404")
	private ModelAndView demyo404Handler(Exception ex, HttpServletResponse response) throws Exception {
		return handleError(ex, response, "core/404", HttpServletResponse.SC_NOT_FOUND);
	}

	/**
	 * Sets the body to be shaded, e.g. because the content should contrast more with the background.
	 * 
	 * @param model The model to change.
	 */
	protected final void setShadedBody(Model model) {
		model.addAttribute(MODEL_KEY_BODY_CLASSES, "dem-shaded-body");
	}

	/**
	 * Sets the layout to an AJAX-compatible format. This layout just dumps the view text as-is.
	 * 
	 * @param model The model to set the layout in.
	 */
	protected final void setLayoutPlain(Model model) {
		model.addAttribute(MODEL_KEY_LAYOUT, LAYOUT_PLAIN);
	}

	/**
	 * Sets the layout to one that does not include a menu, quicksearch, etc: just an application bar.
	 * 
	 * @param model The model to set the layout in.
	 */
	protected final void setLayoutMinimal(Model model) {
		model.addAttribute(MODEL_KEY_LAYOUT, LAYOUT_MINIMAL);
	}

	/**
	 * Sets the translation service into the model.
	 * 
	 * @param model The view model.
	 */
	@ModelAttribute
	private void initTranslationService(Model model) {
		model.addAttribute(MODEL_KEY_I18N_SERV, translationService);
	}

	/**
	 * Sets the reader service into the model.
	 * 
	 * @param model The view model.
	 */
	@ModelAttribute
	private void initReaderService(Model model) {
		model.addAttribute(MODEL_KEY_READER_SERV, readerService);
	}

	/**
	 * Sets the application configuration into the model.
	 * 
	 * @param model The view model.
	 */
	@ModelAttribute
	private void initConfiguration(Model model) {
		model.addAttribute(MODEL_KEY_CONFIG, configService.getConfiguration());
		model.addAttribute(MODEL_KEY_VERSION, SystemConfiguration.getInstance().getVersion());
		model.addAttribute(MODEL_KEY_CODENAME, SystemConfiguration.getInstance().getCodename());
	}

	/**
	 * Sends an HTTP redirect.
	 * 
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @param relativeUrl An URL, relative to the context, to redirect to.
	 * @return Always <code>null</code>.
	 * @deprecated Use {@link #redirect(Model, String)} instead.
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
	 * @param model The Spring model, to remove any unwanted redirect parameters.
	 * @param relativeUrl An URL, relative to the context, to redirect to.
	 * @return The redirection URL.
	 */
	protected String redirect(Model model, String relativeUrl) {
		model.asMap().remove(MODEL_KEY_VERSION);
		model.asMap().remove(MODEL_KEY_CODENAME);
		model.asMap().remove(MODEL_KEY_ASYNC_LESS);
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
	protected final boolean handleLastModified(long reference, HttpServletRequest request,
			HttpServletResponse response) {
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
	protected void download(File file, HttpServletRequest request, HttpServletResponse response) throws IOException {
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
	protected final void download(File file, String filename, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (!file.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			LOGGER.warn("File {} was not found, returning a 404", file);
			return;
		}
		if (handleLastModified(file.lastModified(), request, response)) {
			return;
		}

		response.setDateHeader("Last-Modified", file.lastModified());
		response.setContentLength((int) file.length());
		response.setContentType(mimeTypes.getContentType(file));

		if (filename != null) {
			String targetFilename = URLEncoder.encode(filename, "UTF-8");
			targetFilename = URLDecoder.decode(targetFilename, "ISO8859_1");
			response.setHeader("Content-disposition", "attachment; filename=" + targetFilename);
		}

		try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
			IOUtils.copy(bis, response.getOutputStream());
			response.flushBuffer();
		}
	}

	/**
	 * Prevents the quick search from being displayed in toolbars.
	 * 
	 * @param model The view model.
	 */
	protected final void suppressQuickSearch(Model model) {
		model.addAttribute("suppressQuickSearch", true);
	}

	/**
	 * Sets the flag {@link SystemConfiguration#isLoadLessInAsync()} in the model, as $loadLessInAsync.
	 * 
	 * @param model The view model
	 */
	@ModelAttribute
	private void setLoadLessInAsync(Model model) {
		model.addAttribute(MODEL_KEY_ASYNC_LESS, SystemConfiguration.getInstance().isLoadLessInAsync());
	}

	/**
	 * Gets the value of a parameter as a Long.
	 * 
	 * @param request The request.
	 * @param name The name of the parameter.
	 * @return The value, or <code>null</code> if the parameter is absent or the value is not a Long.
	 */
	protected final Long getLongParam(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value == null) {
			return null;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			LOGGER.warn("Wrong parameter with name '{}': {}", name, value, e);
		}
		return null;
	}

	@InitBinder
	private void initBinder(PropertyEditorRegistry binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}
}
