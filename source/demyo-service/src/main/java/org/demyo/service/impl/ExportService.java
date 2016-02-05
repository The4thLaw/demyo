package org.demyo.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.service.IExportService;
import org.demyo.utils.io.ZipUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IExportService}.
 */
@Service
public class ExportService implements IExportService {
	private static final String EXPORT_DIRECTORY_NAME = "exports";

	private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

	private final List<IExporter> exporters = new ArrayList<>();
	private final File exportDirectory;

	public ExportService() {
		exportDirectory = new File(SystemConfiguration.getInstance().getUserDirectory(), EXPORT_DIRECTORY_NAME);
		exportDirectory.mkdirs();
	}

	@Override
	public void registerExporter(IExporter exporter) {
		LOGGER.debug("Registering exporter of type: {}", exporter.getClass().getCanonicalName());
		exporters.add(exporter);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public File export(boolean withResources) throws DemyoException {
		// Note that we don't check if withResources makes sense for the selected exporter.
		// It's not a issue at the moment.

		IExporter exporter = exporters.get(0);

		File libraryExport = exporter.export();

		if (!withResources) {
			return libraryExport;
		}

		// Build the ZIP file including all resources
		File zipFile = SystemConfiguration.getInstance().createTempFile("demyo-export-archive-", ".dea",
				exportDirectory);

		try (FileOutputStream fos = new FileOutputStream(zipFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ZipOutputStream zos = new ZipOutputStream(bos);) {
			ZipUtils.compress(libraryExport, "demyo." + exporter.getExtension(), zos);
			ZipUtils.compress(SystemConfiguration.getInstance().getImagesDirectory(), "images", zos);
		} catch (IOException e) {
			LOGGER.warn("Failed to export", e);
			throw new DemyoException(DemyoErrorCode.EXPORT_IO_ERROR, e);
		}

		return zipFile;
	}
}
