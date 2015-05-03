package org.demyo.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.demyo.dao.impl.IRawSQLDao;
import org.demyo.model.config.SystemConfiguration;
import org.demyo.model.exception.DemyoErrorCode;
import org.demyo.model.exception.DemyoException;
import org.demyo.service.IImportService;
import org.demyo.service.importing.IImporter;
import org.demyo.utils.io.DIOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract defined by {@link IImportService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1054 $
 */
@Service
public class ImportService implements IImportService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

	@Autowired
	private IRawSQLDao rawSqlDao;
	private List<IImporter> importers = new ArrayList<IImporter>();

	@Override
	public void registerImporter(IImporter importer) {
		LOGGER.debug("Registering importer of type: {}", importer.getClass().getCanonicalName());
		importers.add(importer);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void importFile(String originalFilename, InputStream content) throws DemyoException {
		File importFile = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			// Copy the file to some place we know, so that importers can peek
			importFile = File.createTempFile("demyo-import", ".tmp", SystemConfiguration.getInstance()
					.getTempDirectory());
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
		}
	}
}
