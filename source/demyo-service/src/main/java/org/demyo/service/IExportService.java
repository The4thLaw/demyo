package org.demyo.service;

import org.the4thlaw.commons.exception.CommonException;
import org.the4thlaw.commons.services.exporting.IExporter;

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
	 * @throws CommonException In case of error during export.
	 */
	Output export(boolean withResources) throws CommonException;
}
