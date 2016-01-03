package org.demyo.service.impl;

import java.io.File;

import org.demyo.common.exception.DemyoException;

public interface IExportService {

	public abstract void registerExporter(IExporter exporter);

	public abstract File export() throws DemyoException;

}
