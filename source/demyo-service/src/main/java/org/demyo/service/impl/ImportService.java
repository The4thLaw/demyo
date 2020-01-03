package org.demyo.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.dao.IRawSQLDao;
import org.demyo.service.IImportService;
import org.demyo.service.IReaderContext;
import org.demyo.service.importing.IImporter;
import org.demyo.utils.io.DIOUtils;

/**
 * Implements the contract defined by {@link IImportService}.
 */
@Service
public class ImportService implements IImportService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

	@Autowired
	private IRawSQLDao rawSqlDao;
	@Autowired
	private IReaderContext readerContext;
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
		File importFile = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			// Copy the file to some place we know, so that importers can peek
			importFile = File.createTempFile("demyo-import", ".tmp",
					SystemConfiguration.getInstance().getTempDirectory());
			fos = new FileOutputStream(importFile);
			bos = new BufferedOutputStream(fos);
			IOUtils.copy(content, bos);
			DIOUtils.closeQuietly(bos);

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
			DIOUtils.delete(importFile);
			DIOUtils.closeQuietly(content);
			DIOUtils.closeQuietly(bos);
			DIOUtils.closeQuietly(fos);
			readerContext.clearCurrentReader();
		}
	}
}
