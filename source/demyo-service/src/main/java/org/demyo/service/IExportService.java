package org.demyo.service;

import java.io.File;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.impl.IExporter;

/**
 * This service allows exporting Demyo data to various formats.
 */
public interface IExportService {

	void registerExporter(IExporter exporter);

	File export(boolean withResources) throws DemyoException;
}
