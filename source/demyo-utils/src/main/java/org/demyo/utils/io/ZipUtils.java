package org.demyo.utils.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities to manage ZIP files.
 */
public final class ZipUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);

	private ZipUtils() {

	}

	/**
	 * Extracts a ZIP file to an arbitrary location.
	 * 
	 * @param source The file to extract.
	 * @param destination The destination directory.
	 * @throws IOException In case of error during extraction.
	 */
	public static void extractZip(File source, File destination) throws IOException {
		// Alternative: use ant-compress: http://stackoverflow.com/a/14620551
		if (!destination.exists()) {
			destination.mkdirs();
		}
		if (!destination.isDirectory()) {
			throw new IOException("Destination is not a directory: " + destination.getAbsolutePath());
		}

		LOGGER.debug("Extracting {} to {}", source.getName(), destination.getPath());

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(source);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File entryDestination = new File(destination, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					entryDestination.getParentFile().mkdirs();
					InputStream in = null;
					FileOutputStream fos = null;
					BufferedOutputStream bos = null;
					try {
						in = zipFile.getInputStream(entry);
						fos = new FileOutputStream(entryDestination);
						bos = new BufferedOutputStream(fos);
						IOUtils.copy(in, fos);
					} finally {
						DIOUtils.closeQuietly(in);
						DIOUtils.closeQuietly(bos);
						DIOUtils.closeQuietly(fos);
					}
				}
			}
		} finally {
			DIOUtils.closeQuietly(zipFile);
		}
	}

	/**
	 * Adds a file or directory to a ZIP.
	 * 
	 * @param source The entry to zip.
	 * @param alias An alias to use instead of the name of the entry.
	 * @param destination The ZIP file to populate.
	 * @throws IOException In case of error during compression.
	 */
	public static void compress(File source, String alias, ZipOutputStream destination) throws IOException {
		compress(source, alias, destination, null);
	}

	private static void compress(File source, String alias, ZipOutputStream destination, String currentPath)
			throws IOException {
		String newPath;
		if (alias != null) {
			newPath = alias;
		} else {
			newPath = source.getName();
		}
		if (currentPath != null) {
			newPath = currentPath + "/" + newPath;
		}

		if (source.isDirectory()) {
			for (File f : source.listFiles()) {
				compress(f, null, destination, newPath);
			}
		} else {
			destination.putNextEntry(new ZipEntry(newPath));
			try (InputStream is = new BufferedInputStream(new FileInputStream(source))) {
				IOUtils.copy(is, destination);
			} catch (IOException e) {
				LOGGER.warn("Failed to zip {}", newPath, e);
				throw e;
			}
			destination.closeEntry();
		}
	}
}
