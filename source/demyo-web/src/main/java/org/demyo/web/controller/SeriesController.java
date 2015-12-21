package org.demyo.web.controller;

import java.util.SortedSet;

import org.demyo.model.Series;
import org.demyo.model.util.IdentifyingNameComparator;
import org.demyo.service.IModelServiceNG;
import org.demyo.service.ISeriesService;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link Series} management.
 */
@Controller
@RequestMapping("/series")
public class SeriesController extends AbstractModelControllerNG<Series> {
	@Autowired
	private ISeriesService service;

	/**
	 * Default constructor.
	 */
	public SeriesController() {
		super(Series.class, "series", "series");
	}

	@Override
	protected IModelServiceNG<Series> getService() {
		return service;
	}

	@Override
	protected void fillModelForEdition(Series entity, Model model) {
		if (entity.getId() != null) {
			model.addAttribute("otherSeries", service.findOtherSeries(entity.getId()));
		} else {
			model.addAttribute("otherSeries", service.findAll());
		}
	}

	@InitBinder
	private void initBinder(PropertyEditorRegistry binder) throws Exception {
		registerCollectionEditor(binder, SortedSet.class, "relatedSeries", Series.class,
				new IdentifyingNameComparator());
	}
}
