package org.demyo.desktop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.the4thlaw.commons.utils.fluent.FluentUtils;

/**
 * Manages the registration of plugins.
 */
public class PluginManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

	private final Path[] paths;

	/**
	 * Creates the manager.
	 *
	 * @param paths The paths to scan for plugins.
	 */
	public PluginManager(Path... paths) {
		// Only keep existing paths: missing paths can confuse the servlet environment
		this.paths = Arrays.stream(paths).filter(Files::isDirectory).toArray(Path[]::new);
	}

	/**
	 * Gets all discovered plugins.
	 *
	 * @return The list of plugin files.
	 */
	public List<Path> getPlugins() {
		List<Path> plugins = new ArrayList<>();
		for (Path path : paths) {
			try (Stream<Path> paths = Files.walk(path, Integer.MAX_VALUE)) {
					plugins = paths.filter(FluentUtils.fileWithExtension("jar"))
						.toList();
			} catch (IOException e) {
				LOGGER.warn("Failed to list plugins", e);
			}
		}

		if (LOGGER.isDebugEnabled()) {
			for (Path plugin : plugins) {
				LOGGER.debug("Found plugin {}", plugin);
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
		String joined = Stream.of(paths)
				.map(Path::toAbsolutePath)
				.map(Path::toString)
				.collect(Collectors.joining(","));
		LOGGER.trace("Plugin paths (as string): {}", joined);
		return joined;
	}
}
