package org.demyo.service.impl;

import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.the4thlaw.commons.exception.CommonException;
import org.the4thlaw.commons.services.exporting.ExportOutput;
import org.the4thlaw.commons.services.exporting.IExportService;
import org.the4thlaw.commons.services.exporting.IExporter;
import org.the4thlaw.commons.services.exporting.impl.BaseExportService;
import org.the4thlaw.commons.services.io.IDirectoryService;

/**
 * Implements the contract defined by {@link IExportService}.
 */
@Service
@Validated
public class ExportService extends BaseExportService {
	@Autowired
	private List<IExporter> exporters;

	public ExportService(IDirectoryService directoryService) {
		super(directoryService);
	}

	@PostConstruct
	void init() {
		exporters.forEach(this::registerExporter);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public ExportOutput export(boolean withResources) throws CommonException {
		return super.export(withResources);
	}
}
