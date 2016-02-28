package org.demyo.web.controller;

import java.util.List;
import java.util.SortedSet;

import org.demyo.model.Album;
import org.demyo.model.ModelView;
import org.demyo.model.Series;
import org.demyo.model.util.IdentifyingNameComparator;
import org.demyo.service.IAlbumService;
import org.demyo.service.IModelService;
import org.demyo.service.ISeriesService;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Controller for {@link Series} management.
 */
@Controller
@RequestMapping("/series")
public class SeriesController extends AbstractModelController<Series> {
	@Autowired
	private ISeriesService service;
	@Autowired
	private IAlbumService albumService;

	/**
	 * Default constructor.
	 */
	public SeriesController() {
		super(Series.class, "series", "series");
	}

	@JsonView(ModelView.Minimal.class)
	@RequestMapping(value = "/{id}/albums", method = RequestMethod.GET, consumes = "application/json",
			produces = "application/json")
	public @ResponseBody
	List<Album> getAlbumsForSeries(@PathVariable("id") long seriesId) {
		return albumService.findBySeriesId(seriesId);
	}

	@Override
	protected IModelService<Series> getService() {
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
