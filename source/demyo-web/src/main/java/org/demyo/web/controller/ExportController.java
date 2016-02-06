package org.demyo.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.IExportService;
import org.demyo.service.impl.ExportService.Output;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for export of files.
 */
@Controller
@RequestMapping("/manage/export")
public class ExportController extends AbstractController {
	/**
	 * Support class for export options selection.
	 */
	public static class ExportParameters {
		/** The expected format. */
		private String format = "XML";
		/** The flag indicating whether to include resources. */
		private boolean withResources;

		/**
		 * Gets the expected format.
		 * 
		 * @return the expected format
		 */
		public String getFormat() {
			return format;
		}

		/**
		 * Sets the expected format.
		 * 
		 * @param format the new expected format
		 */
		public void setFormat(String format) {
			this.format = format;
		}

		/**
		 * Checks if is the flag indicating whether to include resources.
		 * 
		 * @return the flag indicating whether to include resources
		 */
		public boolean isWithResources() {
			return withResources;
		}

		/**
		 * Sets the flag indicating whether to include resources.
		 * 
		 * @param withResources the new flag indicating whether to include resources
		 */
		public void setWithResources(boolean withResources) {
			this.withResources = withResources;
		}
	}

	@Autowired
	private IExportService exportService;

	/**
	 * Displays the export options page.
	 * 
	 * @param model The view model.
	 * @return The view name.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showFileSelection(Model model) {
		ExportParameters parameters = new ExportParameters();
		parameters.setWithResources(true); // Set a default (cannot be set in the constructor)
		model.addAttribute("exportOptions", parameters);
		return "manage/export-select";
	}

	/**
	 * Exports a file to a specified format.
	 * 
	 * @param params The export options.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws DemyoException In case of error during export.
	 * @throws IOException In case of error during download.
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void exportFile(ExportParameters params, HttpServletRequest request, HttpServletResponse response)
			throws DemyoException, IOException {
		Output exportedData = exportService.export(params.isWithResources());
		download(exportedData.getFile(), exportedData.getFileName(), request, response);
	}
}
