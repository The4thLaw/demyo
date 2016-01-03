package org.demyo.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.service.IImportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for import of files.
 */
@Controller
@RequestMapping("/manage/import")
public class ImportController extends AbstractController {
	/**
	 * Support class for file upload.
	 */
	public static class FileUpload {
		/** The uploaded file. */
		private MultipartFile uploadedFile;

		/**
		 * Gets the uploaded file.
		 * 
		 * @return the uploaded file
		 */
		public MultipartFile getUploadedFile() {
			return uploadedFile;
		}

		/**
		 * Sets the uploaded file.
		 * 
		 * @param uploadedFile the new uploaded file
		 */
		public void setUploadedFile(MultipartFile uploadedFile) {
			this.uploadedFile = uploadedFile;
		}
	}

	@Autowired
	private IImportService importService;

	/**
	 * Displays the import file selection page.
	 * 
	 * @param model The view model.
	 * @return The view name.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showFileSelection(Model model) {
		model.addAttribute("importFile", new FileUpload());
		return "manage/import-select";
	}

	/**
	 * Imports the selected file.
	 * 
	 * @param upload The file to import.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws DemyoException If import fails.
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String importFile(@ModelAttribute("importFile") FileUpload upload, HttpServletRequest request,
			HttpServletResponse response) throws DemyoException {
		try {
			importService.importFile(upload.getUploadedFile().getOriginalFilename(), upload.getUploadedFile()
					.getInputStream());
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, e);
		}
		return redirect("/");
	}
}
