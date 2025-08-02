package org.demyo.common.config;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;

/**
 * Demyo system configuration. These are things that the user cannot change through the application.
 *
 * @see ApplicationConfiguration
 */
public final class SystemConfiguration {
	private static final String PLUGIN_DIR_NAME = "plugins";
	private static final String CONFIG_KEY_WAR_PATH = "war.path";
	private static final String CONFIG_KEY_CONTEXT_ROOT = "war.contextRoot";
	private static final String CONFIG_KEY_PORTABLE = "portable";
	private static final String CONFIG_KEY_HTTP_ADDRESS = "http.address";
	private static final String CONFIG_KEY_HTTP_PORT = "http.port";
	private static final String CONFIG_KEY_THUMB_MAX_THREADS = "thumbnails.maxThreads";
	private static final String CONFIG_KEY_THUMB_QUEUE_SIZE = "thumbnails.queueSize";
	private static final String APP_NAME = "Demyo";
	private static final int DEFAULT_THUMBNAIL_QUEUE = 40;

	/**
	 * Singleton holder following the solution of Bill Pugh.
	 */
	private static class SingletonHolder {
		private static final SystemConfiguration INSTANCE = new SystemConfiguration();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfiguration.class);
	private static final String SYSTEM_CONFIGURATION_FILENAME = "system.properties";

	/** The version of Demyo. */
	private final String version;
	/** The version codename of Demyo. */
	private final String codename;
	/** The root directory for the Demyo installation. */
	private final Path applicationDirectory;
	/** Path to the WAR file containing the Demyo Web app. */
	private final String warPath;
	/** Context root for the deployment of the WAR file. */
	private final String contextRoot;
	/** The flag indicating whether Demyo is being used in portable mode or not. */
	private final boolean portable;
	/** The listening address for the HTTP server. */
	private final String httpAddress;
	/** The port for the HTTP server. */
	private final int httpPort;
	/** The directory to store user data. */
	private final Path userDirectory;
	/** The path/URL for the database. Similar to the databaseFile but without file extensions. Feed this to H2. */
	private final Path databaseUrlPath;
	/** The file where the database is stored. */
	private final Path databaseFile;
	/** The directory to store user images. */
	private final Path imagesDirectory;
	/** The directory to store temporary files. */
	private final Path tempDirectory;
	/** The directory to store image thumbnails. */
	private final Path thumbnailDirectory;
	/** The directory where system-wide plugins are located. */
	private final Path systemPluginDirectory;
	/** The directory where user-specific directories are located. */
	private final Path userPluginDirectory;
	/** The flag indicating whether to start the Web browser automatically. */
	private final boolean autoStartWebBrowser;
	/** The maximum number of threads that should be used for thumbnails. If left empty, Demyo uses a heuristic. */
	private Integer maxThumbnailThreads;
	/** The maximum number of thumbnails that can be queued while waiting for generation. */
	private int thumbnailQueueSize;
	/** The flag indicating that a flyway repair is required. */
	private boolean flywayRepairRequired;

	/**
	 * Instantiates a new system configuration.
	 */
	private SystemConfiguration() {
		// Find out where we reside
		String path = System.getProperty("demyo.applicationDirectory");
		if (path == null || !Files.isDirectory(Path.of(path, "lib"))) {
			LOGGER.debug("Application directory is set to {} and does not contain a lib directory; "
					+ "defaulting to working directory", path);
			path = System.getProperty("user.dir");
		}
		applicationDirectory = Path.of(path);

		PropertiesConfiguration config = loadConfig();

		version = config.getString("version");
		codename = config.getString("codename");
		warPath = config.getString(CONFIG_KEY_WAR_PATH);
		contextRoot = config.getString(CONFIG_KEY_CONTEXT_ROOT);
		portable = config.getBoolean(CONFIG_KEY_PORTABLE);
		httpAddress = config.getString(CONFIG_KEY_HTTP_ADDRESS);
		httpPort = config.getInt(CONFIG_KEY_HTTP_PORT);
		String threads = config.getString(CONFIG_KEY_THUMB_MAX_THREADS);
		maxThumbnailThreads = StringUtils.isBlank(threads) ? null : Integer.parseInt(threads);
		thumbnailQueueSize = config.getInt(CONFIG_KEY_THUMB_QUEUE_SIZE, DEFAULT_THUMBNAIL_QUEUE);
		autoStartWebBrowser = !config.getBoolean("desktop.noBrowserAutoStart", false);
		systemPluginDirectory = applicationDirectory.resolve(PLUGIN_DIR_NAME);

		// Prepare all paths
		if (portable) {
			userDirectory = applicationDirectory.resolve("data");
			tempDirectory = applicationDirectory.resolve("temp");
		} else {
			Path userDir;
			Path tempDir;

			if (SystemUtils.IS_OS_WINDOWS) {
				// On Windows, try to send the settings to the Application Data folder
				// rather than just the home, if possible
				String baseDirectory = System.getenv("APPDATA");
				if (StringUtils.isBlank(baseDirectory)) {
					baseDirectory = SystemUtils.USER_HOME;
				}
				userDir = Path.of(baseDirectory, APP_NAME);
				tempDir = userDir.resolve("temp");
			} else if (SystemUtils.IS_OS_MAC_OSX) {
				// https://www.google.com/search?q=os+x+"where+to+put+files"
				// https://developer.apple.com/library/mac/#documentation/General/Conceptual/
				// MOSXAppProgrammingGuide/AppRuntime/AppRuntime.html

				userDir = Path.of(SystemUtils.USER_HOME, "Library", "Application Support", APP_NAME);
				tempDir = Path.of(SystemUtils.JAVA_IO_TMPDIR, APP_NAME);
			} else {
				userDir = Path.of(SystemUtils.USER_HOME, ".demyo");
				// Unices may have special temporary directories residing in RAM or being cleaned automatically,
				// so use them
				tempDir = Path.of(SystemUtils.JAVA_IO_TMPDIR, APP_NAME);
			}

			// Allow overriding the user directory
			String systemValue = System.getProperty("demyo.userDirectory");
			if (StringUtils.isNotBlank(systemValue)) {
				userDir = Path.of(systemValue);
				if (SystemUtils.IS_OS_WINDOWS) {
					tempDir = userDir.resolve("temp");
				}
			}

			this.userDirectory = userDir;
			this.tempDirectory = tempDir;
		}

		userPluginDirectory = userDirectory.resolve(PLUGIN_DIR_NAME);
		imagesDirectory = userDirectory.resolve("images");
		thumbnailDirectory = userDirectory.resolve("thumbnails");
		databaseUrlPath = userDirectory.resolve("demyo");
		Path mainDatabaseFile = userDirectory.resolve("demyo.mv.db");
		// Old versions of H2 had a different naming convention, try it as well in case we need to migrate
		Path legacyDatabaseFile = userDirectory.resolve("demyo.h2.db");

		if (!Files.exists(mainDatabaseFile) && Files.exists(legacyDatabaseFile)) {
			databaseFile = legacyDatabaseFile;
		} else {
			databaseFile = mainDatabaseFile;
		}

		createDirectoryIfNeeded(userDirectory);
		createDirectoryIfNeeded(imagesDirectory);
		createDirectoryIfNeeded(tempDirectory);
		createDirectoryIfNeeded(thumbnailDirectory);
		createDirectoryIfNeeded(userPluginDirectory);

		// Logging in info can be pretty useful to detect issues in user setups
		LOGGER.info("The system configuration is: {}", this);
	}

	private Path withOverride(String systemProperty, Path base) {
		String systemValue = System.getProperty(systemProperty);
		if (StringUtils.isNotBlank(systemValue)) {
			return Path.of(systemValue);
		}
		return base;
	}

	/**
	 * Loads the configuration, including defaults and CLI overrides.
	 *
	 * @return The merged configuration.
	 */
	private PropertiesConfiguration loadConfig() {
		// Find the default configuration
		URL defaultConfig = SystemConfiguration.class.getResource("/org/demyo/common/config/system.properties");
		if (defaultConfig == null) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_CONFIG_NO_DEFAULT);
		}

		// Load the system configuration
		Path systemConfigurationFile = applicationDirectory.resolve(SYSTEM_CONFIGURATION_FILENAME);
		PropertiesConfiguration config;
		try {
			// Load defaults
			config = new PropertiesConfiguration(defaultConfig);
			if (Files.isReadable(systemConfigurationFile)) {
				// Load overrides
				LOGGER.debug("Loading configuration from {}", systemConfigurationFile);
				PropertiesConfiguration overrides = new PropertiesConfiguration(systemConfigurationFile.toFile());
				config.copy(overrides);
			} else {
				LOGGER.debug("No system configuration found at {}, relying on defaults", systemConfigurationFile);
			}
		} catch (ConfigurationException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_CONFIG_NOT_READABLE, e);
		}

		// Allow overrides from command line
		for (String key : new String[] { CONFIG_KEY_WAR_PATH, CONFIG_KEY_CONTEXT_ROOT, CONFIG_KEY_PORTABLE,
				CONFIG_KEY_HTTP_ADDRESS, CONFIG_KEY_HTTP_PORT, CONFIG_KEY_THUMB_MAX_THREADS,
				CONFIG_KEY_THUMB_QUEUE_SIZE }) {
			String value = System.getProperty("demyo." + key);
			if (value != null) {
				config.setProperty(key, value);
			}
		}
		return config;
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
	private static void createDirectoryIfNeeded(Path dir) {
		if (Files.isDirectory(dir)) {
			return;
		}
		if (Files.exists(dir)) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_DIR_PATH_ALREADY_TAKEN, dir.toAbsolutePath().toString());
		}
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_DIR_CANNOT_CREATE, e, dir.toAbsolutePath().toString());
		}
	}

	/**
	 * Creates a temporary file in the application temporary directory. The file is marked as to be deleted on exit.
	 *
	 * @param prefix The prefix string to be used in generating the file's name; must be at least three characters long
	 * @return The created file.
	 */
	public Path createTempFile(String prefix) {
		return createTempFile(prefix, null, null);
	}

	/**
	 * Creates a temporary file in the application temporary directory. The file is marked as to be deleted on exit.
	 *
	 * @param prefix The prefix string to be used in generating the file's name; must be at least three characters long
	 * @param suffix The suffix string to be used in generating the file's name; may be <code>null</code>, in which case
	 *            the suffix <code>".tmp"</code> will be used
	 * @return The created file.
	 */
	public Path createTempFile(String prefix, String suffix) {
		return createTempFile(prefix, suffix, null);
	}

	/**
	 * Creates a temporary file in the specified directory. The file is marked as to be deleted on exit.
	 *
	 * @param prefix The prefix string to be used in generating the file's name; must be at least three characters long
	 * @param suffix The suffix string to be used in generating the file's name; may be <code>null</code>, in which case
	 *            the suffix <code>".tmp"</code> will be used
	 * @param directory The directory in which the file is to be created, or <code>null</code> if the default
	 *            temporary-file directory is to be used
	 * @return The created file.
	 */
	public Path createTempFile(String prefix, String suffix, Path directory) {
		if (directory == null) {
			directory = getTempDirectory();
		}

		Path temp;
		try {
			temp = Files.createTempFile(directory, prefix, suffix);
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.IO_GENERIC_ERROR, e);
		}
		temp.toFile().deleteOnExit();
		return temp;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("System configuration:");
		sb.append("\n\tversion: ").append(version);
		sb.append("\n\tcodename: ").append(codename);
		sb.append("\n\tHTTP address: ").append(httpAddress);
		sb.append("\n\tHTTP port: ").append(httpPort);
		sb.append("\n\tstart browser automatically: ").append(autoStartWebBrowser);
		sb.append("\n\tapplication directory: ").append(applicationDirectory);
		sb.append("\n\tWAR path: ").append(warPath);
		sb.append("\n\tcontext root: ").append(contextRoot);
		sb.append("\n\tuser directory: ").append(userDirectory);
		sb.append("\n\tdatabase file: ").append(databaseFile);
		sb.append("\n\timages directory: ").append(imagesDirectory);
		sb.append("\n\tthumbnail directory: ").append(thumbnailDirectory);
		sb.append("\n\ttemporary directory: ").append(tempDirectory);
		sb.append("\n\tsystem plugin directory: ").append(systemPluginDirectory);
		sb.append("\n\tuser plugin directory directory: ").append(userPluginDirectory);
		sb.append("\n\tmaximum thumbnail threads: ").append(maxThumbnailThreads);
		sb.append("\n\tthumbnail queue size: ").append(thumbnailQueueSize);
		return sb.toString();
	}

	/**
	 * Gets the version of Demyo.
	 *
	 * @return the version of Demyo
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the version codename of Demyo.
	 *
	 * @return the version codename of Demyo
	 */
	public String getCodename() {
		return codename;
	}

	/**
	 * Gets the root directory for the Demyo installation.
	 *
	 * @return the root directory for the Demyo installation
	 */
	public Path getApplicationDirectory() {
		return applicationDirectory;
	}

	/**
	 * Gets the path to the WAR file containing the Demyo Web app.
	 *
	 * @return the path to the WAR file containing the Demyo Web app
	 */
	public String getWarPath() {
		return warPath;
	}

	/**
	 * Gets the context root for the deployment of the WAR file.
	 *
	 * @return the context root for the deployment of the WAR file
	 */
	public String getContextRoot() {
		return contextRoot;
	}

	/**
	 * Gets the listening address for the HTTP server.
	 *
	 * @return the listening address for the HTTP server
	 */
	public String getHttpAddress() {
		return httpAddress;
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
	public Path getUserDirectory() {
		return userDirectory;
	}

	/**
	 * Gets the path/URL for the database. Similar to the databaseFile but without file extensions. Feed this to H2.
	 *
	 * @return the path
	 */
	public Path getDatabaseUrlPath() {
		return databaseUrlPath;
	}

	/**
	 * Gets the file to store the database.
	 *
	 * @return the file to store the database
	 */
	public Path getDatabaseFile() {
		return databaseFile;
	}

	/**
	 * Gets the directory to store user images.
	 *
	 * @return the directory to store user images
	 */
	public Path getImagesDirectory() {
		return imagesDirectory;
	}

	/**
	 * Gets the directory to store temporary files, as a {@link Path}.
	 *
	 * @return the directory to store temporary files
	 */
	public Path getTempDirectory() {
		return tempDirectory;
	}

	/**
	 * Gets the directory to store image thumbnails.
	 *
	 * @return the directory to store image thumbnails
	 */
	public Path getThumbnailDirectory() {
		return thumbnailDirectory;
	}

	/**
	 * Checks if is the flag indicating whether to start the Web browser automatically.
	 *
	 * @return the flag indicating whether to start the Web browser automatically
	 */
	public boolean isAutoStartWebBrowser() {
		return autoStartWebBrowser;
	}

	/**
	 * Gets the directory where system-wide plugins are located.
	 *
	 * @return the directory where system-wide plugins are located
	 */
	public Path getSystemPluginDirectory() {
		return systemPluginDirectory;
	}

	/**
	 * Gets the directory where user-specific directories are located.
	 *
	 * @return the directory where user-specific directories are located
	 */
	public Path getUserPluginDirectory() {
		return userPluginDirectory;
	}

	/**
	 * Gets the maximum number of threads that should be used for thumbnails.
	 *
	 * @return the maximum number of threads that should be used for thumbnails
	 */
	public Integer getMaxThumbnailThreads() {
		return maxThumbnailThreads;
	}

	/**
	 * Gets the maximum number of thumbnails that can be queued while waiting for generation.
	 *
	 * @return the maximum number of thumbnails that can be queued while waiting for generation
	 */
	public int getThumbnailQueueSize() {
		return thumbnailQueueSize;
	}

	/**
	 * Checks if is flag indicating that a flyway repair is required.
	 *
	 * @return the flag indicating that a flyway repair is required
	 */
	public boolean isFlywayRepairRequired() {
		return flywayRepairRequired;
	}

	/**
	 * Sets the flag indicating that a flyway repair is required.
	 *
	 * @param requiresFlywayRepair the new flag indicating that a flyway repair is required
	 */
	public void setFlywayRepairRequired(boolean requiresFlywayRepair) {
		this.flywayRepairRequired = requiresFlywayRepair;
	}
}
