package org.demyo.utils.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities to manage ZIP files.
 * 
 * @author $Author: xr $
 * @version $Revision: 1073 $
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
					InputStream in = zipFile.getInputStream(entry);
					FileOutputStream fos = new FileOutputStream(entryDestination);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					try {
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
}
