package org.demyo.web.controller.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.service.IImportService;

/**
 * Controller for import of files.
 */
@RestController
@RequestMapping("/api/manage/import")
public class ImportAPIController {
	@Autowired
	private IImportService importService;

	/**
	 * Imports the selected file.
	 * 
	 * @param upload The file to import.
	 * @return Always <code>true</code>
	 * @throws DemyoException If import fails.
	 */
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public boolean importFile(@RequestParam("importFile") MultipartFile upload) throws DemyoException {
		try {
			importService.importFile(upload.getOriginalFilename(), upload.getInputStream());
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, e);
		}
		return true;
	}
}
