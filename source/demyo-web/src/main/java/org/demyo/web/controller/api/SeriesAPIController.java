package org.demyo.web.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Album;
import org.demyo.model.ModelView;
import org.demyo.model.Series;
import org.demyo.model.filters.DerivativeFilter;
import org.demyo.service.IAlbumService;
import org.demyo.service.IDerivativeService;
import org.demyo.service.ISeriesService;

/**
 * Controller handling the API calls for {@link Series}.
 */
@RestController
@RequestMapping("/api/series")
public class SeriesAPIController extends AbstractModelAPIController<Series> {
	private final IAlbumService albumService;
	private final IDerivativeService derivativeService;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 * @param albumService The service to manage the {@link Album}s.
	 */
	@Autowired
	public SeriesAPIController(ISeriesService service, IAlbumService albumService,
			IDerivativeService derivativeService) {
		super(service);
		this.albumService = albumService;
		this.derivativeService = derivativeService;
	}

	/**
	 * Finds the Albums for a given Series.
	 *
	 * @param seriesId The Series ID
	 * @return The list of associated Albums
	 */
	@JsonView(ModelView.Minimal.class)
	@GetMapping("/{seriesId}/albums")
	public List<Album> getAlbumsForSeries(@PathVariable("seriesId") long seriesId) {
		return albumService.findBySeriesId(seriesId);
	}

	/**
	 * Finds the Albums without Series.
	 *
	 * @return The list of Albums.
	 */
	@JsonView(ModelView.Minimal.class)
	@GetMapping(value = "none/albums")
	public List<Album> getAlbumsWithoutSeries() {
		return albumService.findBySeriesId(null);
	}

	/**
	 * Creates a template for a new Album in a Series.
	 *
	 * @param seriesId The Series ID
	 * @return The list of associated Albums
	 */
	@JsonView(ModelView.AlbumTemplate.class)
	@GetMapping("/{seriesId}/albums/template")
	public Album getAlbumTemplate(@PathVariable("seriesId") long seriesId) {
		return albumService.getAlbumTemplateForSeries(seriesId);
	}

	/**
	 * Counts how many Derivatives use the given Series.
	 * 
	 * @param typeId The internal ID of the {@link Series}
	 * @return the count
	 */
	@GetMapping("{modelId}/derivatives/count")
	public long countDerivativesBySeries(@PathVariable long modelId) {
		return derivativeService.countDerivativesByFilter(DerivativeFilter.forSeries(modelId));
	}
}
