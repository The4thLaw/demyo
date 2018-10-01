package org.demyo.web.controller.api;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.demyo.service.IFilePondService;

/**
 * Controller implementing backend for FilePond.
 */
@RestController
@RequestMapping("/api/filepond")
public class FilePondAPIController {
	@Autowired
	private IFilePondService service;

	/**
	 * Handler for FilePond's "process" method.
	 * 
	 * @param mainFile The user-provided file.
	 * @param otherFile The user-provided file (with another param name).
	 * @return The temporary file identifier.
	 * @throws IOException In case of error while accessing the uploaded file.
	 */
	@PostMapping("/process")
	public String process(@RequestParam(value = "filePondMainImage", required = false) MultipartFile mainFile,
			@RequestParam(value = "filePondOtherImage", required = false) MultipartFile otherFile) throws IOException {
		MultipartFile file;
		if (mainFile != null) {
			file = mainFile;
		} else {
			file = otherFile;
		}
		try (InputStream input = file.getInputStream()) {
			return service.process(file.getOriginalFilename(), file.getInputStream());
		}
	}

	/**
	 * Handler for FilePond's "revert" method.
	 * 
	 * @param fileId The file identifier to revert.
	 */
	@DeleteMapping("/revert")
	public void revert(@RequestBody String fileId) {
		service.revert(fileId);
	}
}
