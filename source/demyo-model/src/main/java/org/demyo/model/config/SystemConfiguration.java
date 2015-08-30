package org.demyo.model.config;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.demyo.model.exception.DemyoErrorCode;
import org.demyo.model.exception.DemyoRuntimeException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demyo system configuration. These are things that the user cannot change through the application.
 * 
 * @see ApplicationConfiguration
 * @author $Author: xr $
 * @version $Revision: 1063 $
 */
// TODO: eventually, we will need to move logs to a proper directory. Do this after switching to logback
public final class SystemConfiguration {
	/**
	 * Singleton holder following the solution of Bill Pugh.
	 * 
	 * @author $Author: xr $
	 * @version $Revision: 1063 $
	 */
	private static class SingletonHolder {
		private static final SystemConfiguration INSTANCE = new SystemConfiguration();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfiguration.class);
	private static final String SYSTEM_CONFIGURATION_FILENAME = "system.properties";
	private static final boolean DEFAULT_PORTABLE_MODE = false;
	private static final int DEFAULT_HTTP_PORT = 8080;

	/** The root directory for the Demyo installation. */
	private final File applicationDirectory;
	/** The flag indicating whether Demyo is being used in portable mode or not. */
	private final boolean portable;
	/** The port for the HTTP server. */
	private final int httpPort;
	/** The directory to store user data. */
	private final File userDirectory;
	/** The file to store the database. */
	private final File databaseFile;
	/** The file to store the application configuration. */
	private final File configurationFile;
	/** The directory to store user images. */
	private final File imagesDirectory;
	/** The directory to store temporary files. */
	private final File tempDirectory;
	/** The directory to store image thumbnails. */
	private final File thumbnailDirectory;

	/**
	 * Instantiates a new system configuration.
	 */
	private SystemConfiguration() {
		// Find out where we reside
		String path = SystemConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath;
		try {
			decodedPath = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_APPDIR_NOT_FOUND, e);
		}
		applicationDirectory = new File(decodedPath);

		// Load the system configuration
		File systemConfigurationFile = new File(applicationDirectory, SYSTEM_CONFIGURATION_FILENAME);
		if (systemConfigurationFile.exists()) {
			LOGGER.debug("Loading configuration from {}", systemConfigurationFile);
		} else {
			LOGGER.debug("No system configuration found at {}, relying on defaults", systemConfigurationFile);
		}
		// Note: configuration silently ignores missing files, so we don't need special behaviour for defaults
		Configuration config;
		try {
			config = new PropertiesConfiguration(systemConfigurationFile);
		} catch (ConfigurationException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_CONFIG_NOT_READABLE, e);
		}
		portable = config.getBoolean("portable", DEFAULT_PORTABLE_MODE);
		httpPort = config.getInt("http.port", DEFAULT_HTTP_PORT);

		// Prepare all paths
		if (portable) {
			userDirectory = new File(applicationDirectory, "data");
			tempDirectory = new File(applicationDirectory, "temp");
		} else {
			if (SystemUtils.IS_OS_WINDOWS) {
				userDirectory = new File(SystemUtils.USER_HOME, "Demyo");
				tempDirectory = new File(userDirectory, "Temp");
			} else if (SystemUtils.IS_OS_MAC_OSX) {
				// https://www.google.com/search?q=os+x+"where+to+put+files"
				// https://developer.apple.com/library/mac/#documentation/General/Conceptual/
				//		MOSXAppProgrammingGuide/AppRuntime/AppRuntime.html
				userDirectory = new File(SystemUtils.USER_HOME + File.separator + "Library" + File.separator
						+ "Application Support", "Demyo");
				tempDirectory = new File(SystemUtils.JAVA_IO_TMPDIR, "Demyo");
			} else {
				userDirectory = new File(SystemUtils.USER_HOME, ".demyo");
				// Unices may have special temporary directories residing in RAM or being cleaned automatically,
				// so use them
				tempDirectory = new File(SystemUtils.JAVA_IO_TMPDIR, "Demyo");
			}
		}
		imagesDirectory = new File(userDirectory, "images");
		thumbnailDirectory = new File(userDirectory, "thumbnails");
		databaseFile = new File(userDirectory, "demyo.h2.db");
		configurationFile = new File(userDirectory, "demyo-config.properties");
		createDirectoryIfNeeded(userDirectory);
		createDirectoryIfNeeded(imagesDirectory);
		createDirectoryIfNeeded(tempDirectory);
		createDirectoryIfNeeded(thumbnailDirectory);

		// Debug our configuration
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(this.toString());
		}
	}

	/**
	 * Gets the configuration instance.
	 * 
	 * @return the configuration.
	 */
	public static SystemConfiguration getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Creates the directory if it doesn't exist already.
	 * 
	 * @param dir The directory to create.
	 */
	private static void createDirectoryIfNeeded(File dir) {
		if (dir.isDirectory()) {
			return;
		}
		if (dir.exists()) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_DIR_PATH_ALREADY_TAKEN, dir.getAbsolutePath());
		}
		if (!dir.mkdirs()) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_DIR_CANNOT_CREATE, dir.getAbsolutePath());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("System configuration:");
		sb.append("\n\tportable: ").append(portable);
		sb.append("\n\tHTTP port: ").append(httpPort);
		sb.append("\n\tapplication directory: ").append(applicationDirectory);
		sb.append("\n\tuser directory: ").append(userDirectory);
		sb.append("\n\tdatabase file: ").append(databaseFile);
		sb.append("\n\tconfiguration file: ").append(configurationFile);
		sb.append("\n\timages directory: ").append(imagesDirectory);
		sb.append("\n\ttemporary directory: ").append(tempDirectory);
		return sb.toString();
	}

	/**
	 * Gets the root directory for the Demyo installation.
	 * 
	 * @return the root directory for the Demyo installation
	 */
	public File getApplicationDirectory() {
		return applicationDirectory;
	}

	/**
	 * Gets the port for the HTTP server.
	 * 
	 * @return the port for the HTTP server
	 */
	public int getHttpPort() {
		return httpPort;
	}

	/**
	 * Gets the directory to store user data.
	 * 
	 * @return the directory to store user data
	 */
	public File getUserDirectory() {
		return userDirectory;
	}

	/**
	 * Gets the file to store the database.
	 * 
	 * @return the file to store the database
	 */
	public File getDatabaseFile() {
		return databaseFile;
	}

	public File getConfigurationFile() {
		return configurationFile;
	}

	/**
	 * Gets the directory to store user images.
	 * 
	 * @return the directory to store user images
	 */
	public File getImagesDirectory() {
		return imagesDirectory;
	}

	/**
	 * Gets the directory to store temporary files.
	 * 
	 * @return the directory to store temporary files
	 */
	public File getTempDirectory() {
		return tempDirectory;
	}

	/**
	 * Gets the directory to store image thumbnails.
	 * 
	 * @return the directory to store image thumbnails
	 */
	public File getThumbnailDirectory() {
		return thumbnailDirectory;
	}
}
