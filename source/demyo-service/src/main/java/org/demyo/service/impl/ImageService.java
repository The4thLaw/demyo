package org.demyo.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.demyo.dao.IImageDao;
import org.demyo.dao.IModelDao;
import org.demyo.model.Image;
import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.model.config.SystemConfiguration;
import org.demyo.model.exception.DemyoErrorCode;
import org.demyo.model.exception.DemyoException;
import org.demyo.model.exception.DemyoRuntimeException;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IImageService;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link IImageService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1075 $
 */
@Service
public class ImageService extends AbstractModelService<Image> implements IImageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

	@Autowired
	private IImageDao dao;
	@Autowired
	private IConfigurationService configService;

	/**
	 * Default constructor.
	 */
	public ImageService() {
		super(Image.class);
	}

	@Override
	protected IModelDao<Image> getDao() {
		return dao;
	}

	@Override
	public File getImageFile(String path) throws DemyoException {
		File image = new File(SystemConfiguration.getInstance().getImagesDirectory(), path);
		validateImagePath(image);
		if (!image.isFile()) {
			throw new DemyoException(DemyoErrorCode.IMAGE_NOT_FOUND, path);
		}
		return image;
	}

	@Override
	public File getImageThumbnail(long id, String path) throws DemyoException {
		// Check cache
		File thumb = new File(SystemConfiguration.getInstance().getThumbnailDirectory(), id + ".jpg");
		if (thumb.exists()) {
			return thumb;
		}

		// No cache hit, generate thumbnail
		File image = getImageFile(path);
		long time = System.currentTimeMillis();
		BufferedImage buffImage;
		try {
			buffImage = ImageIO.read(image);
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_IO_ERROR, e);
		}
		ApplicationConfiguration config = configService.getConfiguration();
		BufferedImage buffThumb = Scalr.resize(buffImage, Method.BALANCED, Mode.AUTOMATIC,
				config.getThumbnailWidth(), config.getThumbnailHeight(), Scalr.OP_ANTIALIAS);
		buffImage.flush();
		LOGGER.debug("Thumbnail generated in {}ms", System.currentTimeMillis() - time);

		try {
			ImageIO.write(buffThumb, "jpg", thumb);
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_IO_ERROR, e);
		}
		buffThumb.flush();

		return thumb;
	}

	private void validateImagePath(File image) throws DemyoException {
		try {
			String imagesDirectoryPath = SystemConfiguration.getInstance().getImagesDirectory().getCanonicalPath();
			String imagePath = image.getCanonicalPath();
			if (!imagePath.startsWith(imagesDirectoryPath)) {
				throw new DemyoException(DemyoErrorCode.IMAGE_DIRECTORY_TRAVERSAL, imagePath);
			}
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_IO_ERROR, e);
		}
	}
}
