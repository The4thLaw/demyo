package org.demyo.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.model.Image;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;

/**
 * Controller for {@link Image} management.
 */
@Controller
@RequestMapping("/images")
public class ImagesController extends AbstractModelController<Image> {
	private static final CacheControl CACHE_FOR_IMAGES = CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic();

	/**
	 * Support class for file uploads.
	 */
	public static class FileUpload {
		/** The uploaded files. */
		private List<MultipartFile> uploadedFiles;

		/**
		 * Gets the uploaded files.
		 * 
		 * @return the uploaded files
		 */
		public List<MultipartFile> getUploadedFiles() {
			return uploadedFiles;
		}

		/**
		 * Sets the uploaded files.
		 * 
		 * @param uploadedFiles the new uploaded files
		 */
		public void setUploadedFiles(List<MultipartFile> uploadedFiles) {
			this.uploadedFiles = uploadedFiles;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ImagesController.class);

	@Autowired
	private IImageService imageService;

	/**
	 * Default constructor.
	 */
	public ImagesController() {
		super(Image.class, "images", "image");
	}

	/**
	 * Gets an image file. The path is retrieved from the database.
	 * 
	 * @param imageId The image ID.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws DemyoException If the image is not readable, not found, etc.
	 * @throws IOException If the image cannot be sent to the client.
	 */
	@RequestMapping(value = "/full/{imageId}/**", method = RequestMethod.GET)
	public void getFullImage(@PathVariable("imageId") long imageId, HttpServletRequest request,
			HttpServletResponse response) throws DemyoException, IOException {
		Image image = imageService.getByIdForEdition(imageId);
		File imageFile = imageService.getImageFile(image);
		download(imageFile, request, response);
	}

	/**
	 * Gets the thumbnail for an image. The path is retrieved from the URL.
	 * 
	 * @param imageId The image ID.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws DemyoException If the image is not readable, not found, thumbnail generation fails, etc.
	 * @throws IOException If the image cannot be sent to the client.
	 */
	@RequestMapping(value = "/thumbnail/{imageId}/**", method = RequestMethod.GET)
	public void getThumbnailImage(@PathVariable("imageId") long imageId, HttpServletRequest request,
			HttpServletResponse response) throws DemyoException, IOException {
		File imageFile = imageService.getImageThumbnail(imageId);
		download(imageFile, request, response);
	}

	/**
	 * @see IImageService#getImage(long, Optional, boolean)
	 */
	@RequestMapping(value = "/{imageId}/file/**", method = RequestMethod.GET)
	public ResponseEntity<Resource> getImageFile(@PathVariable("imageId") long imageId,
			@RequestParam("w") Optional<Integer> maxWidth,
			@RequestParam(value = "lenient", defaultValue = "false") boolean lenient)
			throws DemyoException, IOException {
		Resource res = imageService.getImage(imageId, maxWidth, lenient);
		// TODO: When migrating to Spring 5, rely on MediaTypeFactory
		String type = mimeTypes.getContentType(res.getFile());
		return ResponseEntity.ok().cacheControl(CACHE_FOR_IMAGES).contentType(MediaType.valueOf(type))
				/*.contentType(MediaTypeFactory.getMediaType(res).get())*/.body(res);
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	public String index(@RequestParam("id") List<Long> ids, Model model) {

		List<Image> images = new ArrayList<>(ids.size());

		for (long id : ids) {
			images.add(imageService.getByIdForView(id));
		}

		model.addAttribute("imageList", images);
		model.addAttribute("isFullList", true);

		return "images/index";
	}

	/**
	 * Displays the image upload page.
	 * 
	 * @param model The model
	 * @return The view name
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String addFromUpload(Model model) {
		model.addAttribute("imageUpload", new FileUpload());
		return "images/upload";
	}

	/**
	 * Uploads image(s) in the library.
	 * 
	 * @param upload The content of the submitted form.
	 * @param model The model.
	 * @return The view name
	 * @throws DemyoException In case of error during upload.
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uploadFile(@ModelAttribute("imageUpload") FileUpload upload, Model model) throws DemyoException {
		SystemConfiguration config = SystemConfiguration.getInstance();
		List<Long> addedImages = new ArrayList<>(upload.getUploadedFiles().size());
		for (MultipartFile file : upload.getUploadedFiles()) {
			if (file.getSize() <= 0) {
				// Skipping empty files also allows recovering from no selected file at all
				continue;
			}
			// Store the file in a temporary location
			File temp = config.createTempFile("image-upload-");
			try {
				file.transferTo(temp);
			} catch (IllegalStateException | IOException e) {
				throw new DemyoException(DemyoErrorCode.IMAGE_UPLOAD_ERROR, e);
			}
			addedImages.add(imageService.uploadImage(file.getOriginalFilename(), temp));
		}

		if (addedImages.isEmpty()) {
			return "images/upload";
		} else if (addedImages.size() == 1) {
			return redirect(model, "/images/view/" + addedImages.get(0));
		} else {
			model.addAttribute("id", addedImages);
			return redirect(model, "/images/list");
		}
	}

	/**
	 * Detects the list of all images that are currently on the disk, but not registered in Demyo.
	 * 
	 * @param model The model
	 * @return The view name
	 */
	@RequestMapping(value = "/detect", method = RequestMethod.GET)
	public String detectImagesOnDisk(Model model) {
		model.addAttribute("imagePaths", imageService.findUnknownDiskImages());
		return "images/detect";
	}

	/**
	 * Adds the specified detected images.
	 * 
	 * @param request The HTTP request
	 * @param model The model
	 * @return The view name
	 * @throws DemyoException In case of error while adding the specified images
	 */
	@RequestMapping(value = "/detect", method = RequestMethod.POST)
	public String addDetectedImages(HttpServletRequest request, Model model) throws DemyoException {
		String[] paths = request.getParameterValues("paths");
		List<Long> addedImages = new ArrayList<>(paths.length);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Selected images: {}", Arrays.asList(paths));
		}

		for (String path : paths) {
			addedImages.add(imageService.addExistingImage(path));
		}

		if (addedImages.isEmpty()) {
			return redirect(model, "/images/detect");
		} else if (addedImages.size() == 1) {
			return redirect(model, "/images/view/" + addedImages.get(0));
		} else {
			model.addAttribute("id", addedImages);
			return redirect(model, "/images/list");
		}
	}

	@Override
	protected IModelService<Image> getService() {
		return imageService;
	}
}
