package org.demyo.web.controller;

import java.util.List;
import java.util.SortedSet;

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

import org.demyo.model.Album;
import org.demyo.model.ModelView;
import org.demyo.model.Series;
import org.demyo.model.util.IdentifyingNameComparator;
import org.demyo.service.IAlbumService;
import org.demyo.service.IModelService;
import org.demyo.service.ISeriesService;

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

	@Override
	protected void onView(Series series, Model model) {
		boolean hasAlbumsNotInWishlist = false;
		for (Album a : series.getAlbums()) {
			if (!a.isWishlist()) {
				hasAlbumsNotInWishlist = true;
			}
		}
		model.addAttribute("readingListIsRelevant", hasAlbumsNotInWishlist);
	}

	/**
	 * Finds the Albums for a given Series.
	 * 
	 * @param seriesId The Series ID
	 * @return The list of associated Albums
	 */
	@JsonView(ModelView.Minimal.class)
	@RequestMapping(value = "/{seriesId}/albums", method = RequestMethod.GET, //
			consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Album> getAlbumsForSeries(@PathVariable("seriesId") long seriesId) {
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
