package org.demyo.desktop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the registration of plugins.
 */
public class PluginManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

	private final File[] paths;

	/**
	 * Creates the manager.
	 * 
	 * @param paths The paths to scan for plugins.
	 */
	public PluginManager(File... paths) {
		this.paths = paths;
	}

	/**
	 * Gets all discovered plugins.
	 * 
	 * @return The list of plugin files.
	 */
	public List<File> getPlugins() {
		List<File> plugins = new ArrayList<>();
		for (File path : paths) {
			if (!path.isDirectory()) {
				LOGGER.info("{} is not a directory, skipping it", path.getAbsolutePath());
			} else {
				plugins.addAll(FileUtils.listFiles(path, new String[] { "jar" }, true));
			}
		}

		if (LOGGER.isDebugEnabled()) {
			for (File plugin : plugins) {
				LOGGER.debug("Found plugin: " + plugin.getPath());
			}
		}

		return plugins;
	}

	/**
	 * Returns the discovered plugins, as a comma-separated list of absolute paths.
	 * 
	 * @return The CSV paths.
	 */
	public String getPluginPaths() {
		// TODO: (java 8) use a lambda for this
		StringBuilder sb = new StringBuilder();
		for (File plugin : getPlugins()) {
			sb.append(plugin.getAbsolutePath()).append(",");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}
}
