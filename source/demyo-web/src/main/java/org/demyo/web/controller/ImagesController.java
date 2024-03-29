package org.demyo.web.controller;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.the4thlaw.commons.services.image.ImageRetrievalResponse;

import org.demyo.common.exception.DemyoException;
import org.demyo.service.IImageService;

/**
 * Controller to access image files.
 */
@Controller
@RequestMapping("/images")
public class ImagesController extends AbstractController {
	private static final CacheControl CACHE_FOR_IMAGES = CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic();
	private static final CacheControl NO_CACHE = CacheControl.noCache();

	@Autowired
	private IImageService imageService;

	/**
	 * @see IImageService#getImage(long, Optional, boolean)
	 */
	@GetMapping("/{imageId}/file/**")
	public ResponseEntity<Resource> getImageFile(@PathVariable("imageId") long imageId,
			@RequestParam("w") Optional<Integer> maxWidth,
			@RequestParam(value = "lenient", defaultValue = "true") boolean lenient)
			throws DemyoException {

		ImageRetrievalResponse image = imageService.getImage(imageId, maxWidth, lenient);
		Resource res = image.getResource();

		Optional<MediaType> media = MediaTypeFactory.getMediaType(res);
		BodyBuilder schwarzenegger = ResponseEntity.ok();
		if (media.isPresent()) {
			schwarzenegger.contentType(media.get());
		}
		if (image.isExact()) {
			schwarzenegger.cacheControl(CACHE_FOR_IMAGES);
		} else {
			schwarzenegger.cacheControl(NO_CACHE);
		}
		return schwarzenegger.body(res);
	}
}
