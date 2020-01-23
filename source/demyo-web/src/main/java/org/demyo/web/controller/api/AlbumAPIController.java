package org.demyo.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.demyo.model.Album;
import org.demyo.service.IAlbumService;

/**
 * Controller handling the API calls for {@link Albums}s.
 */
@RestController
@RequestMapping("/api/albums")
public class AlbumAPIController extends AbstractModelAPIController<Album> {
	@Autowired
	public AlbumAPIController(IAlbumService service) {
		super(Album.class, service);
	}
}
