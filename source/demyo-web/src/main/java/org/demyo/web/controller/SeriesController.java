package org.demyo.web.controller;

import java.util.Set;

import org.demyo.model.Series;
import org.demyo.service.IModelService;
import org.demyo.service.ISeriesService;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for {@link Series} management.
 * 
 * @author $Author: xr $
 * @version $Revision: 1071 $
 */
@Controller
@RequestMapping("/series")
public class SeriesController extends AbstractModelController<Series> {
	@Autowired
	private ISeriesService service;

	/**
	 * Default constructor.
	 */
	public SeriesController() {
		super(Series.class, "series", "series");
	}

	@Override
	protected IModelService<Series> getService() {
		return service;
	}

	@Override
	protected void fillModelForEdition(Series entity, Model model) {
		Criterion criterion = null;
		if (entity.getId() != null) {
			criterion = Restrictions.ne("id", entity.getId());
		}
		model.addAttribute("otherSeries", service.findAll(criterion));
	}

	@InitBinder
	private void initBinder(PropertyEditorRegistry binder) throws Exception {
		registerCollectionEditor(binder, Set.class, "relatedSeries", Series.class);
	}
}
