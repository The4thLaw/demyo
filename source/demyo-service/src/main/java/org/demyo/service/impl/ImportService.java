package org.demyo.service.impl;

import java.io.InputStream;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.the4thlaw.commons.exception.CommonException;
import org.the4thlaw.commons.services.importing.IImportService;
import org.the4thlaw.commons.services.importing.IImporter;
import org.the4thlaw.commons.services.importing.impl.BaseImportService;
import org.the4thlaw.commons.services.io.IDirectoryService;

import org.demyo.dao.IRawSQLDao;

/**
 * Implements the contract defined by {@link IImportService}.
 */
@Service
@Validated
public class ImportService extends BaseImportService<IRawSQLDao> {
	@Autowired
	private List<IImporter> importers;

	public ImportService(IDirectoryService directoryService, IRawSQLDao databaseDao) {
		super("Demyo", directoryService, databaseDao);
	}

	@PostConstruct
	void init() {
		importers.forEach(this::registerImporter);
	}


	@Transactional(rollbackFor = Throwable.class)
	// Don't forget to clear the caches when importing.
	// If the number of caches grows, see http://stackoverflow.com/a/41022526
	@CacheEvict(cacheNames = "ModelLists", allEntries = true)
	@Override
	public void importFile(@NotEmpty String originalFilename, @NotNull InputStream content) throws CommonException {
		super.importFile(originalFilename, content);
	}
}
