package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Series;
import org.demyo.service.ISeriesService;

@RestController
@RequestMapping("/api/series")
public class SeriesAPIController extends AbstractModelAPIController<Series> {
	private final ISeriesService service;

	@Autowired
	public SeriesAPIController(ISeriesService service) {
		super(Series.class);
		this.service = service;
	}

	@Override
	protected ISeriesService getService() {
		return service;
	}

}
