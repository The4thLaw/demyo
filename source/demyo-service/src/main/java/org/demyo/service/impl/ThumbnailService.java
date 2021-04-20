package org.demyo.service.impl;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;
import org.demyo.service.IThumbnailService;
import org.demyo.service.ImageRetrievalResponse;
import org.demyo.service.ThumbnailGenerationOverload;
import org.demyo.utils.io.DIOUtils;
import org.demyo.utils.io.ImageUtils;

/**
 * Implements the contract defined by {@link IThumbnailService}.
 */
@Service
public class ThumbnailService implements IThumbnailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThumbnailService.class);
	private static final Pattern THUMB_DIR_PATTERN = Pattern.compile("^\\d+w$");
	private static final double LENIENCY_WIDTH_FACTOR = 1.2;
	private static final int THREAD_POOL_RATE = 60 * 60 * 1000;
	/** The absolute maximum number of thumb threads that can run in parallel. */
	private static final int MAX_RUNNING_THUMBS = 10;
	/**
	 * Timeout for thumbnail generation. Firefox and Chrome's default request timeouts are 300 seconds but we don't want
	 * to wait that long.
	 */
	private static final int THUMB_TIMEOUT_SECONDS = 150;

	@FunctionalInterface
	public interface ImageSupplier {
		File getImage() throws DemyoException;
	}

	private final ThreadPoolExecutor executor;
	private final File thumbnailDirectory;

	/**
	 * Default constructor.
	 */
	public ThumbnailService() {
		this(SystemConfiguration.getInstance().getThumbnailDirectory(),
				SystemConfiguration.getInstance().getThumbnailQueueSize());
	}

	/**
	 * Constructor allowing to set the thumbnail directory and queue size.
	 * 
	 * @param thumbnailDirectory The directory where thumbnails are stored.
	 * @param queueSize The size of the thumbnail generation queue.
	 */
	public ThumbnailService(File thumbnailDirectory, int queueSize) {
		this.thumbnailDirectory = thumbnailDirectory;

		// Another option would be to use a LIFO but it seems like it will be pretty confusing for users
		// (see https://stackoverflow.com/a/8272674/109813)
		executor = new ThreadPoolExecutor(0, 1, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(queueSize));
		executor.allowCoreThreadTimeOut(true);
		setThumbnailPoolSize();
	}

	@Override
	@Scheduled(initialDelay = THREAD_POOL_RATE, fixedRate = THREAD_POOL_RATE)
	public void setThumbnailPoolSize() {
		Integer systemMaxThreads = SystemConfiguration.getInstance().getMaxThumbnailThreads();
		if (systemMaxThreads != null) {
			LOGGER.info("Setting thumbnail pool size: fixed = {}", systemMaxThreads);
			executor.setMaximumPoolSize(systemMaxThreads);
			executor.setCorePoolSize(systemMaxThreads);
			return;
		}

		int cores = Runtime.getRuntime().availableProcessors();
		long memory = Runtime.getRuntime().maxMemory();

		// Allow at most one thumbnail thread per two cores
		int coreLimit = cores / 2;
		// Allow at most one thumbnail thread per (roughly) 256MB of RAM
		long memoryLimit = memory / (255_000_000);
		// Take the minimum of those two, constrained
		int maxThreads = (int) Math.min(Math.min(coreLimit, memoryLimit), MAX_RUNNING_THUMBS);

		LOGGER.info("Setting thumbnail pool size: core = {}, memory = {}, final = {}", coreLimit, memoryLimit,
				maxThreads);
		// Gotcha: the maximum pool size is only used when the queue is full. What we need is a fixed pool size
		// where the core threads can time out
		executor.setMaximumPoolSize(maxThreads);
		executor.setCorePoolSize(maxThreads);
	}

	@Override
	public ImageRetrievalResponse getThumbnail(long id, int maxWidth, boolean lenient, ImageSupplier imageFileLoader)
			throws DemyoException {
		File directoryBySize = new File(thumbnailDirectory, maxWidth + "w");

		// Check cache (two possible formats - jpg is more likely so check it first)
		ImageRetrievalResponse cached = getCachedThumbnail(directoryBySize, id);
		if (cached != null) {
			return cached;
		}

		// No cache hit, check for leniency
		File image = imageFileLoader.getImage();
		int originalWidth = ImageUtils.getImageWidth(image);
		if (maxWidth >= originalWidth || (lenient && maxWidth * LENIENCY_WIDTH_FACTOR >= originalWidth)) {
			LOGGER.debug("Leniently returning the original image for {}, it's {}px wide instead of the requested {}",
					id, originalWidth, maxWidth);
			// Return the original image, we don't have anything larger or the requested width is close enough
			// to the original not to warrant the creation of a resized version
			return new ImageRetrievalResponse(image);
		}

		/*
		No cache hit, generate thumbnail.
		Thumbnails are generated in parallel threads so that we can limit the number of ongoing generations.
		However, we still block the request while waiting for the result because the browser is expecting the
		thumbnail.
		This is just a way to limit resource usage in constrained environments. It impacts the user experience
		but without this, we could just kill the JVM with OutOfMemoryErrors...
		 */
		long submissionTime = System.currentTimeMillis();

		try {
			Future<ImageRetrievalResponse> submission = executor
					.submit(() -> generateThumbnail(id, image, maxWidth, directoryBySize, submissionTime));
			LOGGER.trace("Thumbnail generation submitted for image {} at width {}", id, maxWidth);
			logThumbnailExecutorStats();
			return submission.get(THUMB_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.warn("Interrupted while generating a thumbnail for image {} at width {}", id, maxWidth, e);
			throw new DemyoException(DemyoErrorCode.IMAGE_IO_ERROR, "Interrupted during thumbnail generation");
		} catch (ExecutionException | TimeoutException | RejectedExecutionException e) {
			LOGGER.warn(
					"Failed to generate a thumbnail for image {} at width {}, will attempt to provide a fallback. Reason is: {}",
					id, maxWidth, e.getMessage());
			return getFallbackThumbnail(id, maxWidth);
		}
	}

	private static ImageRetrievalResponse getCachedThumbnail(File directoryBySize, long id) {
		File jpgThumb = new File(directoryBySize, id + ".jpg");
		if (jpgThumb.exists()) {
			return new ImageRetrievalResponse(jpgThumb);
		}
		File pngThumb = new File(directoryBySize, id + ".png");
		if (pngThumb.exists()) {
			return new ImageRetrievalResponse(pngThumb);
		}
		return null;
	}

	private ImageRetrievalResponse getFallbackThumbnail(long id, int maxWidth)
			throws ThumbnailGenerationOverload {
		List<Integer> availableWidths = Stream//
				.of(thumbnailDirectory
						.listFiles(f -> f.isDirectory() && THUMB_DIR_PATTERN.matcher(f.getName()).matches()))
				// Keep only the names
				.map(File::getName)
				// Parse the width
				.map(f -> Integer.parseInt(f.substring(0, f.length() - 1)))
				// Sort so that the closest to maxWidth comes first
				.sorted((a, b) -> Math.abs(maxWidth - a) - Math.abs(maxWidth - b))
				.collect(Collectors.toList());

		LOGGER.trace("Found the following possible thumbnail sizes: {}", availableWidths);

		for (int width : availableWidths) {
			File directoryBySize = new File(thumbnailDirectory, width + "w");
			ImageRetrievalResponse cached = getCachedThumbnail(directoryBySize, id);
			if (cached != null) {
				LOGGER.debug("Found a fallback thumbnail for image {} at size {} instead of size {}", id, width,
						maxWidth);
				cached.setExact(false);
				return cached;
			}
		}

		// If all else fails, abort
		throw new ThumbnailGenerationOverload("Could not find a fallback thumbnail for image" + id);
	}

	private ImageRetrievalResponse generateThumbnail(long id, File image, int maxWidth,
			File directoryBySize,
			long submissionTime) throws DemyoException {
		// If the task was submitted but the request timed out, just complete the task without doing anything
		long secondsSinceSubmission = (System.currentTimeMillis() - submissionTime) / 1000;
		if (secondsSinceSubmission > THUMB_TIMEOUT_SECONDS) {
			LOGGER.debug(
					"Discarded thubmnail generation for image {} at width {}: "
							+ "the request timed out in the meantime (submitted {} seconds ago)",
					id, maxWidth, secondsSinceSubmission);
			return null;
		}

		BufferedImage buffImage;
		try {
			buffImage = ImageIO.read(image);
		} catch (IOException e) {
			throw new DemyoException(DemyoErrorCode.SYS_IO_ERROR, e);
		}

		long time = System.currentTimeMillis();
		logThumbnailExecutorStats();
		LOGGER.trace("Generating thumbnail for image {} at width {}", id, maxWidth);

		// Avoid creating the directory if we return the original image
		if (!directoryBySize.isDirectory()) {
			directoryBySize.mkdirs();
			LOGGER.debug("Creating thumbnail directory: {}", directoryBySize);
		} else {
			LOGGER.trace("Thumbnail directory exists: {}", directoryBySize);
		}

		BufferedImage buffThumb = Scalr.resize(buffImage, Method.ULTRA_QUALITY, Mode.FIT_TO_WIDTH, maxWidth, 0,
				Scalr.OP_ANTIALIAS);
		LOGGER.debug("Thumbnail for {} generated in {}ms", id, System.currentTimeMillis() - time);

		File jpgThumb = new File(directoryBySize, id + ".jpg");
		File pngThumb = new File(directoryBySize, id + ".png");
		try {
			// Write opaque images as JPG, transparent images as PNG
			if (Transparency.OPAQUE == buffThumb.getTransparency()) {
				ImageIO.write(buffThumb, "jpg", jpgThumb);
				return new ImageRetrievalResponse(jpgThumb);
			} else {
				ImageIO.write(buffThumb, "png", pngThumb);
				return new ImageRetrievalResponse(pngThumb);
			}
		} catch (IOException e) {
			// Ensure we don't store invalid contents
			DIOUtils.delete(jpgThumb);
			DIOUtils.delete(pngThumb);
			throw new DemyoException(DemyoErrorCode.IMAGE_IO_ERROR, e);
		} finally {
			buffImage.flush();
			buffThumb.flush();
		}
	}

	private void logThumbnailExecutorStats() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Thumbnail executor stats: {} active, {} in pool (max: {}), {} queued",
					executor.getActiveCount(),
					executor.getPoolSize(), executor.getMaximumPoolSize(), executor.getQueue().size());
		}
	}
}
