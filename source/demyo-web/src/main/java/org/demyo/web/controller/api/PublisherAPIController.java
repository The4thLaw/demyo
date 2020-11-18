package org.demyo.web.controller.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import org.demyo.model.Collection;
import org.demyo.model.ModelView;
import org.demyo.model.Publisher;
import org.demyo.model.filters.AlbumFilter;
import org.demyo.service.IAlbumService;
import org.demyo.service.ICollectionService;
import org.demyo.service.IPublisherService;

/**
 * Controller handling the API calls for {@link Publisher}s.
 */
@RestController
@RequestMapping("/api/publishers")
public class PublisherAPIController extends AbstractModelAPIController<Publisher> {
	private final IPublisherService service;
	private final ICollectionService collectionService;
	private final IAlbumService albumService;

	/**
	 * Creates the controller.
	 * 
	 * @param service The service to manage the entries.
	 * @param collectionService The service to manage {@link Collections}.
	 * @param albumService The service to manage Albums.
	 */
	@Autowired
	public PublisherAPIController(IPublisherService service, ICollectionService collectionService,
			IAlbumService albumService) {
		super(service);
		this.service = service;
		this.collectionService = collectionService;
		this.albumService = albumService;
	}

	@Override
	@GetMapping({ "/", "/index" })
	public MappingJacksonValue index(@RequestParam("view") Optional<String> view) {
		Iterable<Publisher> value = service.findAllForIndex();
		return getIndexView(view, value);
	}

	/**
	 * Counts how many Albums use the given Binding.
	 * 
	 * @param modelId The internal ID of the Binding
	 * @return the count
	 */
	@GetMapping("{modelId}/albums/count")
	public long countAlbumsByPublisher(@PathVariable long modelId) {
		return albumService.countAlbumsByFilter(AlbumFilter.forPublisher(modelId));
	}

	/**
	 * Finds the Collections for a given Publisher.
	 * 
	 * @param publisherId The Publisher ID
	 * @return The Publisher's Collections
	 */
	@JsonView(ModelView.Minimal.class)
	@GetMapping("/{publisherId}/collections")
	public List<Collection> getCollectionsForPublisher(@PathVariable("publisherId") long publisherId) {
		return collectionService.findByPublisherId(publisherId);
	}
}
