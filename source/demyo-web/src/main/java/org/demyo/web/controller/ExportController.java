package org.demyo.web.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.impl.IExportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for export of files
 */
@Controller
@RequestMapping("/manage/export")
public class ExportController extends AbstractController {
	@Autowired
	IExportService exportService;

	@RequestMapping(method = RequestMethod.GET)
	public void exportFile(HttpServletRequest request, HttpServletResponse response) throws DemyoException,
			IOException {
		File exportedFile = exportService.export();
		// TODO: file name
		download(exportedFile, request, response);
	}
}
