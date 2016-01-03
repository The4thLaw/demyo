package org.demyo.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.model.Image;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.service.IImageService;
import org.demyo.service.IModelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Controller for {@link Image} management.
 */
@Controller
@RequestMapping("/images")
public class ImagesController extends AbstractModelController<Image> {
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
		 * @param uploadedFile the new uploaded files
		 */
		public void setUploadedFiles(List<MultipartFile> uploadedFiles) {
			this.uploadedFiles = uploadedFiles;
		}
	}

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
	 * @param id The image ID.
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws DemyoException If the image is not readable, not found, etc.
	 * @throws IOException If the image cannot be sent to the client.
	 */
	@RequestMapping(value = "/full/{id}/**", method = RequestMethod.GET)
	public void getFullImage(@PathVariable("id") long id, HttpServletRequest request, HttpServletResponse response)
			throws DemyoException, IOException {
		Image image = imageService.getByIdForEdition(id);
		File imageFile = imageService.getImageFile(image);
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
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String uploadFile(@ModelAttribute("imageUpload") FileUpload upload, Model model) {
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
				throw new DemyoRuntimeException(DemyoErrorCode.IMAGE_UPLOAD_ERROR, e);
			}
			addedImages.add(imageService.uploadImage(file.getOriginalFilename(), temp));
		}

		if (addedImages.isEmpty()) {
			return "images/upload";
		} else if (addedImages.size() == 1) {
			return redirect("/images/view/" + addedImages.get(0));
		} else {
			model.addAttribute("id", addedImages);
			return redirect("/images/list");
		}
	}

	@Override
	protected IModelService<Image> getService() {
		return imageService;
	}
}
