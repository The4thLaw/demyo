package org.demyo.utils.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
	private static final int SNIFF_DEFAULT_BUFFER = 256;

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
			LOGGER.warn("Failed to close stream", e);
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
	 * Deletes a file, but logs if deletion failed. If deletion failed, the file is marked for deletion on exit, in case
	 * the lock has disappeared in the mean time.
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

		try {
			Files.delete(file.toPath());
		} catch (IOException e) {
			LOGGER.warn("Failed to delete file at {}", file.getAbsolutePath(), e);
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
			return fileName
					.substring(lastDot + 1, fileName.length())
					.replaceAll("[^A-Za-z0-9]", "")
					.toLowerCase();
		}
	}

	/**
	 * Sniffs the first bytes of a file, and tries to match them to a specific pattern.
	 * 
	 * @param file The file to sniff
	 * @param byteCount The maximum number of bytes to sniff
	 * @param charset The excepted character set of the file
	 * @param pattern The pattern to match
	 * @return <code>true</code> if sniffing was successfull and the sniffed content matches the pattern.
	 *         <code>false</code> otherwise.
	 */
	public static boolean sniffFile(File file, int byteCount, Charset charset, Pattern pattern) {
		try (FileInputStream input = new FileInputStream(file)) {
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
	 * @return <code>true</code> if sniffing was successfull and the sniffed content matches the pattern.
	 *         <code>false</code> otherwise.
	 */
	public static boolean sniffFile(File file, Pattern pattern) {
		return sniffFile(file, SNIFF_DEFAULT_BUFFER, StandardCharsets.UTF_8, pattern);
	}
}
