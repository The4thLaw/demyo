package org.demyo.desktop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.h2.engine.ConnectionInfo;
import org.h2.engine.Constants;
import org.h2.jdbc.JdbcConnection;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;

/**
 * Heavily inspired by {@link org.h2.tools.Upgrade}, but relying on a local repository of libraries instead, and solving
 * some file access lock issues.
 * <p>
 * The base Upgrade tool is Copyright 2004-2022 H2 Group. Multiple-Licensed under the MPL 2.0, and the EPL 1.0
 * (https://h2database.com/html/license.html).
 * </p>
 */
public class H2LocalUpgrade {
	/**
	 * Performs database upgrade from an older version of H2.
	 *
	 * @param url the JDBC connection URL
	 * @param info the connection properties ("user", "password", etc).
	 * @param version the old version of H2
	 * @return {@code true} on success, {@code false} if URL is a remote or in-memory URL
	 * @throws SQLException on failure
	 * @throws IOException on failure
	 * @throws ReflectiveOperationException on failure
	 */
	public static boolean upgrade(String url, Properties info, int version)
			throws SQLException, IOException, ReflectiveOperationException {
		Properties oldInfo = new Properties();
		oldInfo.putAll(info);
		Object password = info.get("password");
		if (password instanceof char[]) {
			oldInfo.put("password", ((char[]) password).clone());
		}
		ConnectionInfo ci = new ConnectionInfo(url, info, null, null);
		if (!ci.isPersistent() || ci.isRemote()) {
			return false;
		}
		String name = ci.getName();

		// Copy the database to work on a specific version: file locks will sometimes not be released, causing migration
		// issues
		String workingCopyName = ci.getName() + "-migration";
		copy(name, workingCopyName);

		String script = name + ".script.sql";
		StringBuilder oldUrl = new StringBuilder("jdbc:h2:").append(workingCopyName).append(";ACCESS_MODE_DATA=r");
		copyProperty(ci, oldUrl, "FILE_LOCK");
		copyProperty(ci, oldUrl, "MV_STORE");
		String cipher = copyProperty(ci, oldUrl, "CIPHER");
		String scriptCommandSuffix = cipher == null ? "" : " CIPHER AES PASSWORD '" + UUID.randomUUID() + "' --hide--";
		java.sql.Driver driver = loadH2(version);
		try (Connection conn = driver.connect(oldUrl.toString(), oldInfo)) {
			conn.createStatement().execute(StringUtils.quoteStringSQL(new StringBuilder("SCRIPT TO "), script)
					.append(scriptCommandSuffix).toString());
		} finally {
			unloadH2(driver);
		}
		rename(name, false);
		try (JdbcConnection conn = new JdbcConnection(url, info, null, null, false)) {
			StringBuilder builder = StringUtils.quoteStringSQL(new StringBuilder("RUNSCRIPT FROM "), script)
					.append(scriptCommandSuffix);
			if (version <= 200) {
				builder.append(" FROM_1X");
			}
			conn.createStatement().execute(builder.toString());
		} catch (Throwable t) {
			rename(name, true);
			throw t;
		} finally {
			Files.deleteIfExists(Paths.get(script));
			// We delete the working copy but keep the backup just in case
			Path workingMv = Path.of(workingCopyName + Constants.SUFFIX_MV_FILE);
			Path workingLob = Path.of(workingCopyName + ".lobs.db");
			try {
				Files.deleteIfExists(workingMv);
				Files.deleteIfExists(workingLob);
			} catch (IOException e) {
				// Do nothing, it's not a big deal
				workingMv.toFile().deleteOnExit();
				workingLob.toFile().deleteOnExit();
			}
		}
		return true;
	}

	private static void rename(String name, boolean back) throws IOException {
		rename(name, Constants.SUFFIX_MV_FILE, back);
		rename(name, ".lobs.db", back);
	}

	private static void rename(String name, String suffix, boolean back) throws IOException {
		String source = name + suffix;
		String target = source + ".bak";
		if (back) {
			String t = source;
			source = target;
			target = t;
		}
		Path p = Paths.get(source);
		if (Files.exists(p)) {
			Files.move(p, Paths.get(target), StandardCopyOption.ATOMIC_MOVE);
		}
	}

	private static void copy(String source, String target) throws IOException {
		copy(source, target, Constants.SUFFIX_MV_FILE);
		copy(source, target, ".lobs.db");
	}

	private static void copy(String source, String target, String suffix) throws IOException {
		Path sourcePath = Path.of(source + suffix);
		Path targetPath = Path.of(target + suffix);
		if (Files.exists(sourcePath)) {
			Files.copy(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private static String copyProperty(ConnectionInfo ci, StringBuilder oldUrl, String name) {
		try {
			String value = ci.getProperty(name, null);
			if (value != null) {
				oldUrl.append(';').append(name).append('=').append(value);
			}
			return value;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Loads the specified version of H2 in a separate class loader.
	 *
	 * @param version the version to load
	 * @return the driver of the specified version
	 * @throws IOException on I/O exception
	 * @throws ReflectiveOperationException on exception during initialization of the driver
	 */
	public static java.sql.Driver loadH2(int version) throws IOException, ReflectiveOperationException {
		String prefix;
		if (version >= 201) {
			if ((version & 1) != 0 || version > Constants.BUILD_ID) {
				throw new IllegalArgumentException("version=" + version);
			}
			prefix = "2.0.";
		} else if (version >= 177) {
			prefix = "1.4.";
		} else if (version >= 146 && version != 147) {
			prefix = "1.3.";
		} else if (version >= 120) {
			prefix = "1.2.";
		} else {
			throw new IllegalArgumentException("version=" + version);
		}
		String fullVersion = prefix + version;
		byte[] data = loadFromDisk(fullVersion);
		ZipInputStream is = new ZipInputStream(new ByteArrayInputStream(data));
		HashMap<String, byte[]> map = new HashMap<>(version >= 198 ? 2048 : 1024);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (ZipEntry ze; (ze = is.getNextEntry()) != null;) {
			if (ze.isDirectory()) {
				continue;
			}
			IOUtils.copy(is, baos);
			map.put(ze.getName(), baos.toByteArray());
			baos.reset();
		}
		ClassLoader cl = new ClassLoader(null) {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				String resourceName = name.replace('.', '/') + ".class";
				byte[] b = map.get(resourceName);
				if (b == null) {
					return ClassLoader.getSystemClassLoader().loadClass(name);
				}
				return defineClass(name, b, 0, b.length);
			}

			@Override
			public InputStream getResourceAsStream(String name) {
				byte[] b = map.get(name);
				return b != null ? new ByteArrayInputStream(b) : null;
			}
		};
		return (java.sql.Driver) cl.loadClass("org.h2.Driver").getDeclaredMethod("load").invoke(null);
	}

	private static byte[] loadFromDisk(String fullVersion) {
		String h2cacheProperty = System.getProperty("demyo.h2.cacheDirectoryName", "legacy-h2-versions");
		Path h2CacheDirectory = SystemConfiguration.getInstance().getApplicationDirectory()
				.resolve(h2cacheProperty);

		Path h2Jar = h2CacheDirectory.resolve("h2-" + fullVersion + ".jar");
		if (!Files.isReadable(h2Jar)) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_MISSING_H2_FOR_MIGRATION, "No file at " + h2Jar);
		}
		try {
			return Files.readAllBytes(h2Jar);
		} catch (IOException e) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_MISSING_H2_FOR_MIGRATION, e,
					"Failed to read the file at " + h2Jar);
		}
	}

	/**
	 * Unloads the specified driver of H2.
	 *
	 * @param driver the driver to unload
	 * @throws ReflectiveOperationException on exception
	 */
	public static void unloadH2(java.sql.Driver driver) throws ReflectiveOperationException {
		driver.getClass().getDeclaredMethod("unload").invoke(null);
	}

	private H2LocalUpgrade() {
	}
}
