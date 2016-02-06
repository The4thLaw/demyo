package org.demyo.service;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.exporting.IExporter;
import org.demyo.service.impl.ExportService.Output;

/**
 * This service allows exporting Demyo data to various formats.
 */
public interface IExportService {

	/**
	 * Registers an exporter to the service.
	 * 
	 * @param exporter The exporter to register.
	 */
	void registerExporter(IExporter exporter);

	/**
	 * Exports the library.
	 * 
	 * @param withResources Whether to include resources in this export.
	 * @return The export output.
	 * @throws DemyoException In case of error during export.
	 */
	Output export(boolean withResources) throws DemyoException;
}
