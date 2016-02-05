package org.demyo.utils.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General I/O utilities.
 * 
 * @author $Author: xr $
 * @version $Revision: 1058 $
 */
// Starts with a 'D' for 'Demyo', to allow usage in parallel with Apache's version
public final class DIOUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(DIOUtils.class);

	private DIOUtils() {
	}

	/**
	 * Unconditionally close a {@link Closeable}.
	 * <p>
	 * Equivalent to {@link Closeable#close()}, except any exceptions will be logged and ignored. This is typically
	 * used in finally blocks.
	 * </p>
	 * <p>
	 * Similar to Apache Commons' method, but actually logs any error rather than discarding them.
	 * </p>
	 * 
	 * @param closeable The object to close, may be null or already closed
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (IOException e) {
			LOGGER.warn("Failed to close stream", e);
		}
	}

	/**
	 * Unconditionally close a {@link ZipFile}.
	 * <p>
	 * Equivalent to {@link ZipFile#close()}, except any exceptions will be logged and ignored. This is typically
	 * used in finally blocks.
	 * </p>
	 * <p>
	 * Similar to Apache Commons' method, but actually logs any error rather than discarding them.
	 * </p>
	 * 
	 * @param closeable The object to close, may be null or already closed
	 */
	public static void closeQuietly(ZipFile closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (IOException e) {
			LOGGER.warn("Failed to close stream", e);
		}
	}

	/**
	 * Unconditionally close an {@link XMLStreamWriter}.
	 * <p>
	 * Equivalent to {@link XMLStreamWriter#close()}, except any exceptions will be logged and ignored. This is
	 * typically used in finally blocks.
	 * </p>
	 * <p>
	 * Similar to Apache Commons' method, but actually logs any error rather than discarding them.
	 * </p>
	 * 
	 * @param closeable The object to close, may be null or already closed
	 */
	public static void closeQuietly(XMLStreamWriter closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (XMLStreamException e) {
			LOGGER.warn("Failed to close stream", e);
		}
	}

	/**
	 * Deletes a file, but logs if deletion failed. If deletion failed, the file is marked for deletion on exit, in
	 * case the lock has disappeared in the mean time.
	 * <p>
	 * This method ignores the file if it is not a regular file.
	 * </p>
	 * 
	 * @param file The file to delete.
	 */
	public static void delete(File file) {
		if (!file.exists() || !file.isFile()) {
			LOGGER.debug("Doesn't exist or not a regular file: {}", file.getAbsolutePath());
			return;
		}
		if (!file.delete()) {
			LOGGER.warn("Failed to delete file at {}", file.getAbsolutePath());
			file.deleteOnExit();
		}
	}

	/**
	 * Deletes a directory recursively, and log if deletion failed but don't throw an exception.
	 * 
	 * @param directory The directory to delete.
	 */
	public static void deleteDirectory(File directory) {
		if (directory == null) {
			return;
		}
		LOGGER.debug("Deleting directory at {}", directory);
		try {
			FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
			LOGGER.warn("Failed to delete directory at {}", directory, e);
		}
	}

	/**
	 * Creates a temporary directory at the specified location.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name.
	 * @param suffix The suffix string to be used in generating the file's name; may be <code>null</code>, in which
	 *        case the suffix <code>".tmp"</code> will be used. Note that {@code .d} will be appended in all cases.
	 * @param directory The directory in which the file is to be created, or <code>null</code> if the default
	 *        temporary-file directory is to be used
	 * @return A {@link File} pointing to a temporary directory.
	 * @throws IOException In case of error while creating the temporary directory.
	 */
	// This suffers from a notorious race condition, which could be used by malicious programs. However, the risk is
	// quite low. Eventually, move to Java 7 and use java.nio.file.Files.createTempDirectory 
	public static File createTempDirectory(String prefix, String suffix, File directory) throws IOException {

		File tempFile = File.createTempFile(prefix, suffix, directory);
		File tempDir = new File(tempFile.getParentFile(), tempFile.getName() + ".d");

		if (!(tempFile.delete())) {
			throw new IOException("Could not delete temp file: " + tempFile.getAbsolutePath());
		}

		if (!(tempDir.mkdir())) {
			throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
		}

		return tempDir;
	}

	/**
	 * Returns the extension of a file.
	 * 
	 * @param fileName The name of the file.
	 * @return The extension, or <code>null</code> if there was no extension.
	 */
	public static String getFileExtension(String fileName) {
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot <= 0) {
			return null;
		} else {
			return fileName.substring(lastDot + 1, fileName.length());
		}
	}
}
