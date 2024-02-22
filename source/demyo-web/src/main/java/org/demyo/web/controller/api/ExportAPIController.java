package org.demyo.web.controller.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
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
	public HttpEntity<Resource> exportFile(@RequestParam("format") String format, @RequestParam("withResources") boolean withResources)
			throws DemyoException, IOException {
		Output exportedData = exportService.export(withResources);
		Path file = exportedData.getFile();

		HttpHeaders headers = new HttpHeaders();

		headers.setLastModified(Files.getLastModifiedTime(file).toMillis());
		headers.setContentLength(Files.size(file));

		headers.setContentType(
				MediaTypeFactory.getMediaType(file.getFileName().toString()).orElse(MediaType.APPLICATION_OCTET_STREAM));

		ContentDisposition contentDisp = ContentDisposition.builder("attachment")
				.filename(exportedData.getFileName(), StandardCharsets.UTF_8)
				.build();
		headers.setContentDisposition(contentDisp);

		return new HttpEntity<>(new FileSystemResource(file), headers);
	}
}
