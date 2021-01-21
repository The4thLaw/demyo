package org.demyo.web.controller.api;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.IExportService;
import org.demyo.service.impl.ExportService.Output;

/**
 * Controller for export of files.
 */
@RestController
@RequestMapping("/api/manage/export")
public class ExportAPIController {
	@Autowired
	private IExportService exportService;

	/**
	 * Exports a file to a specified format.
	 * 
	 * @param format The export format.
	 * @param withResources Whether to include resources in the export.
	 * @throws DemyoException In case of error during export.
	 * @throws IOException In case of error during download.
	 * @return The
	 */
	@GetMapping
	public HttpEntity<Resource> exportFile(@RequestParam String format, @RequestParam boolean withResources)
			throws DemyoException, IOException {
		Output exportedData = exportService.export(withResources);
		File file = exportedData.getFile();

		HttpHeaders headers = new HttpHeaders();

		headers.setLastModified(file.lastModified());
		headers.setContentLength(file.length());

		// TODO [Spring 5] Use MediaTypeFactory
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		// TODO [Spring 5] use https://stackoverflow.com/a/53857047
		String targetFilename = URLEncoder.encode(exportedData.getFileName(), "UTF-8");
		targetFilename = URLDecoder.decode(targetFilename, "ISO8859_1");
		headers.add("Content-disposition", "attachment; filename=" + targetFilename);

		return new HttpEntity<Resource>(new FileSystemResource(file), headers);
	}
}