package org.demyo.utils.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General I/O utilities.
 */
// Starts with a 'D' for 'Demyo', to allow usage in parallel with Apache's version
public final class DIOUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(DIOUtils.class);
	private static final String STREAM_CLOSE_ERROR = "Failed to close stream";
	private static final int SNIFF_DEFAULT_BUFFER = 256;
	private static final Pattern FILE_EXT_EXCLUSIONS = Pattern.compile("[^A-Za-z0-9]");

	private DIOUtils() {
	}

	/**
	 * Unconditionally close a {@link Closeable}.
	 * <p>
	 * Equivalent to {@link Closeable#close()}, except any exceptions will be logged and ignored. This is typically used
	 * in finally blocks.
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
			LOGGER.warn(STREAM_CLOSE_ERROR, e);
		}
	}

	/**
	 * Unconditionally close a {@link ZipFile}.
	 * <p>
	 * Equivalent to {@link ZipFile#close()}, except any exceptions will be logged and ignored. This is typically used
	 * in finally blocks.
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
			LOGGER.warn(STREAM_CLOSE_ERROR, e);
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
			LOGGER.warn(STREAM_CLOSE_ERROR, e);
		}
	}

	/**
	 * Deletes a file, but logs if deletion failed. If deletion failed, the file is marked for deletion on exit, in case
	 * the lock has disappeared in the mean time.
	 * <p>
	 * This method ignores the file if it is not a regular file.
	 * </p>
	 * 
	 * @param file The file to delete.
	 */
	public static void delete(File file) {
		delete(file.toPath());
	}

	/**
	 * Deletes a file, but logs if deletion failed. If deletion failed, the file is marked for deletion on exit, in case
	 * the lock has disappeared in the mean time.
	 * <p>
	 * This method ignores the file if it is not a regular file.
	 * </p>
	 * 
	 * @param file The file to delete.
	 */
	public static void delete(Path file) {
		if (!(Files.exists(file) && Files.isRegularFile(file))) {
			LOGGER.debug("Doesn't exist or not a regular file: {}", file.toAbsolutePath());
			return;
		}

		try {
			Files.delete(file);
		} catch (IOException e) {
			LOGGER.warn("Failed to delete file at {}", file.toAbsolutePath(), e);
			file.toFile().deleteOnExit();
		}
	}

	/**
	 * Deletes a directory recursively, and log if deletion failed but don't throw an exception.
	 * 
	 * @param directory The directory to delete.
	 */
	public static void deleteDirectory(Path directory) {
		if (directory == null) {
			return;
		}
		deleteDirectory(directory.toFile());
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
	 * @param directory The directory in which the file is to be created, or <code>null</code> if the default
	 *            temporary-file directory is to be used
	 * @return A {@link File} pointing to a temporary directory.
	 * @throws IOException In case of error while creating the temporary directory.
	 */
	public static File createTempDirectory(String prefix, File directory) throws IOException {
		return Files.createTempDirectory(directory.toPath(), prefix).toFile();
	}

	/**
	 * Returns the extension of a file.
	 * <p>
	 * Special characters are stripped out to avoid potential injections
	 * </p>
	 * 
	 * @param fileName The name of the file.
	 * @return The extension, or <code>null</code> if there was no extension.
	 */
	public static String getFileExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot <= 0) {
			return null;
		} else {
			String baseExt = fileName.substring(lastDot + 1, fileName.length()).toLowerCase();
			return FILE_EXT_EXCLUSIONS.matcher(baseExt).replaceAll("");
		}
	}

	/**
	 * Sniffs the first bytes of a file, and tries to match them to a specific pattern.
	 * 
	 * @param file The file to sniff
	 * @param byteCount The maximum number of bytes to sniff
	 * @param charset The excepted character set of the file
	 * @param pattern The pattern to match
	 * @return <code>true</code> if sniffing was successful and the sniffed content matches the pattern.
	 *         <code>false</code> otherwise.
	 */
	public static boolean sniffFile(Path file, int byteCount, Charset charset, Pattern pattern) {
		try (InputStream input = Files.newInputStream(file)) {
			byte[] buffer = new byte[byteCount];
			int count = IOUtils.read(input, buffer);
			LOGGER.debug("Sniffed the first {} bytes of {}", count, file);

			String readString = new String(buffer, charset);
			if (pattern.matcher(readString).matches()) {
				return true;
			}
		} catch (IOException e) {
			LOGGER.warn("Failed to sniff {}, assuming no match", file, e);
		}
		return false;
	}

	/**
	 * Sniffs the first few bytes of a file, and tries to match them as UTF-8 to a specific pattern.
	 * 
	 * @param file The file to sniff
	 * @param pattern The pattern to match
	 * @return <code>true</code> if sniffing was successful and the sniffed content matches the pattern.
	 *         <code>false</code> otherwise.
	 */
	public static boolean sniffFile(Path file, Pattern pattern) {
		return sniffFile(file, SNIFF_DEFAULT_BUFFER, StandardCharsets.UTF_8, pattern);
	}

	/**
	 * Ensures that a child path is indeed child to the provided parent path.
	 * 
	 * @param parent The parent path.
	 * @param child The child path.
	 * @throws SecurityException if the assertion fails.
	 */
	public static void assertChildOf(Path parent, Path child) {
		Path absoluteParent = parent.toAbsolutePath().normalize();
		Path absoluteChild = child.toAbsolutePath().normalize();
		if (!absoluteChild.startsWith(absoluteParent)) {
			throw new SecurityException("Attempted directory traversal: " + parent + " is not a parent of " + child);
		}
	}
}
