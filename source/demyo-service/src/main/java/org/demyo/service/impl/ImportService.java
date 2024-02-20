package org.demyo.service.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.the4thlaw.commons.utils.io.FileUtils;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.service.IImportService;
import org.demyo.service.importing.IImporter;

/**
 * Implements the contract defined by {@link IImportService}.
 */
@Service
public class ImportService implements IImportService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

	@Autowired
	private IRawSQLDao rawSqlDao;
	@Autowired
	private List<IImporter> importers = new Vector<>();

	@Override
	public void registerImporter(IImporter importer) {
		LOGGER.debug("Registering importer of type: {}", importer.getClass().getCanonicalName());
		importers.add(importer);
	}

	@Transactional(rollbackFor = Throwable.class)
	// Don't forget to clear the caches when importing.
	// If the number of caches grows, see http://stackoverflow.com/a/41022526
	@CacheEvict(cacheNames = "ModelLists", allEntries = true)
	@Override
	public void importFile(@NotEmpty String originalFilename, @NotNull InputStream content) throws DemyoException {
		Path importFile = null;
		OutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			// Copy the file to some place we know, so that importers can peek
			importFile = Files.createTempFile(SystemConfiguration.getInstance().getTempDirectory(), "demyo-import",
					".tmp");
			fos = Files.newOutputStream(importFile);
			bos = new BufferedOutputStream(fos);
			content.transferTo(bos);
			org.the4thlaw.commons.utils.io.IOUtils.closeQuietly(bos);

			// Detect importer to use
			IImporter importer = null;
			for (IImporter imp : importers) {
				if (imp.supports(originalFilename, importFile)) {
					importer = imp;
					LOGGER.debug("Found a supporting importer of type: {}", importer.getClass().getCanonicalName());
					break;
				}
			}
			if (importer == null) {
				throw new DemyoException(DemyoErrorCode.IMPORT_FORMAT_NOT_SUPPORTED);
			}

			// Clear the database
			rawSqlDao.pruneAllTables();

			// Perform actual import
			importer.importFile(originalFilename, importFile);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.IMPORT_IO_ERROR, e);
		} finally {
			FileUtils.deleteQuietly(importFile);
			org.the4thlaw.commons.utils.io.IOUtils.closeQuietly(content);
			org.the4thlaw.commons.utils.io.IOUtils.closeQuietly(bos);
			org.the4thlaw.commons.utils.io.IOUtils.closeQuietly(fos);
		}
	}
}
