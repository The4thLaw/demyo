package org.demyo.web.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.IExportService;

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
		private String format;
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
		model.addAttribute("exportOptions", new ExportParameters());
		return "manage/export-select"; // TODO: create the view
	}

	@RequestMapping(method = RequestMethod.POST)
	public void exportFile(ExportParameters params, HttpServletRequest request, HttpServletResponse response)
			throws DemyoException, IOException {
		File exportedFile = exportService.export(params.isWithResources());
		// TODO: the export service should return an expected file name as well as the data
		download(exportedFile, "demyo-export.dea", request, response);
	}
}
