package org.demyo.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipOutputStream;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.service.IExportService;
import org.demyo.service.exporting.IExporter;
import org.demyo.utils.io.ZipUtils;

/**
 * Implements the contract defined by {@link IExportService}.
 */
@Service
public class ExportService implements IExportService {
	/**
	 * Defines the result of an export.
	 */
	public static final class Output {
		/** The {@link File} containing the data. */
		private final File file;
		/** The name of the file to provide to the user. */
		private final String fileName;

		Output(File file, String fileName) {
			this.file = file;
			this.fileName = fileName;
		}

		public File getFile() {
			return file;
		}

		public String getFileName() {
			return fileName;
		}
	}

	private static final String EXPORT_DIRECTORY_NAME = "exports";
	private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private final List<IExporter> exporters = new Vector<>();
	private final Path exportDirectory;

	public ExportService() throws IOException {
		exportDirectory = SystemConfiguration.getInstance().getUserDirectory().toPath().resolve(EXPORT_DIRECTORY_NAME);
		Files.createDirectories(exportDirectory);
	}

	@Override
	public void registerExporter(@NotNull IExporter exporter) {
		LOGGER.debug("Registering exporter of type: {}", exporter.getClass().getCanonicalName());
		exporters.add(exporter);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Output export(boolean withResources) throws DemyoException {
		// Note that we don't check if withResources makes sense for the selected exporter.
		// It's not a issue at the moment.

		IExporter exporter = exporters.get(0);
		String baseExportFileName = "demyo_" + LocalDate.now().format(DATE_FORMAT) + ".";

		File libraryExport = exporter.export();
		LOGGER.debug("Data export complete");

		if (!withResources) {
			return new Output(libraryExport, baseExportFileName + exporter.getExtension(false));
		}

		LOGGER.debug("Adding resources to export");
		// Build the ZIP file including all resources
		File zipFile = SystemConfiguration.getInstance().createTempFile("demyo-export-archive-",
				"." + exporter.getExtension(true), exportDirectory);

		try (FileOutputStream fos = new FileOutputStream(zipFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ZipOutputStream zos = new ZipOutputStream(bos)) {
			// The file inside the archive must always have the same name to be imported back
			ZipUtils.compress(libraryExport, "demyo." + exporter.getExtension(false), zos);
			ZipUtils.compress(SystemConfiguration.getInstance().getImagesDirectory(), "images", zos);
		} catch (IOException e) {
			LOGGER.warn("Failed to export", e);
			throw new DemyoException(DemyoErrorCode.EXPORT_IO_ERROR);
		}

		LOGGER.debug("All resources added, export is fully complete");

		return new Output(zipFile, baseExportFileName + exporter.getExtension(true));
	}
}
