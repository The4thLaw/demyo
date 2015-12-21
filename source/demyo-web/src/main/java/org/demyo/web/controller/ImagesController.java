package org.demyo.web.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.demyo.model.Image;
import org.demyo.model.exception.DemyoException;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Controller for {@link Image} management.
 */
@Controller
@RequestMapping("/images")
public class ImagesController extends AbstractModelController<Image> {
	@Autowired
	private IImageService imageService;

	/**
	 * Default constructor.
	 */
	public ImagesController() {
		super(Image.class, "images", "image");
	}

	/**
	 * Gets an image file. The path is retrieved from the URL.
	 * 
	 * @param id The image ID.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws DemyoException If the image is not readable, not found, etc.
	 * @throws IOException If the image cannot be sent to the client.
	 */
	@RequestMapping(value = "/full/{id}/**", method = RequestMethod.GET)
	public void getFullImage(@PathVariable("id") long id, HttpServletRequest request, HttpServletResponse response)
			throws DemyoException, IOException {
		String imagePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		imagePath = imagePath.substring(("/images/full/" + id + "/").length());
		File imageFile = imageService.getImageFile(imagePath);
		download(imageFile, request, response);
	}

	/**
	 * Gets the thumbnail for an image. The path is retrieved from the URL.
	 * 
	 * @param id The image ID.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws DemyoException If the image is not readable, not found, thumbnail generation fails, etc.
	 * @throws IOException If the image cannot be sent to the client.
	 */
	@RequestMapping(value = "/thumbnail/{id}/**", method = RequestMethod.GET)
	public void getThumbnailImage(@PathVariable("id") long id, HttpServletRequest request,
			HttpServletResponse response) throws DemyoException, IOException {
		String imagePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		imagePath = imagePath.substring(("/images/thumbnail/" + id + "/").length());
		File imageFile = imageService.getImageThumbnail(id, imagePath);
		download(imageFile, request, response);
	}

	@Override
	protected IModelService<Image> getService() {
		return imageService;
	}
}
