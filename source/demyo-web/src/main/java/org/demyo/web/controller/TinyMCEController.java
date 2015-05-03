package org.demyo.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Port of the official GZip compressor to a Spring 3 controller.
 * <p>
 * Removes configuration of some information: cache is always on, plugin is always initialised properly, there is
 * no suffix (use minified script), always compress, always include core. Stores the cache in a proper location.
 * Uses Spring's MD5 helper. Use IOUtils to read file contents. All methods are private. Assumes no custom files.
 * </p>
 * <p>
 * Appart from that, most of the structure is untouched, and a bit ugly.
 * </p>
 * <p>
 * The original code is copyright 2009, Moxiecode Systems AB, released under LGPL License.
 * </p>
 * 
 * @author $Author: xr $
 * @version $Revision: 1034 $
 */
@Controller
@RequestMapping("/tiny_mce")
public class TinyMCEController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TinyMCEController.class);
	private static final long EXPIRES_OFFSET = 3600 * 24 * 10 * 1000; // cache for 10 days in browser

	private final String cachePath;
	@Autowired
	private ServletContext context;

	/**
	 * Default constructor.
	 */
	public TinyMCEController() {
		// TODO: use demyo cache location
		cachePath = System.getProperty("java.io.tmpdir");
	}

	/**
	 * Gets the gzipped TinyMCE components.
	 * 
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws IOException For various I/O errors.
	 */
	@RequestMapping("/tiny_mce.js.gz")
	public void getGzipFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ServletOutputStream outStream = response.getOutputStream();

		// Get input
		String[] plugins = getParam(request, "plugins", "").split(",");
		String[] languages = getParam(request, "languages", "").split(",");
		String[] themes = getParam(request, "themes", "").split(",");

		// Custom extra javascripts to pack

		// Headers
		response.setContentType("text/javascript");
		response.addHeader("Vary", "Accept-Encoding"); // Handle proxies
		response.setDateHeader("Expires", System.currentTimeMillis() + EXPIRES_OFFSET);

		// Setup cache info
		String cacheKey = getParam(request, "plugins", "") + getParam(request, "languages", "")
				+ getParam(request, "themes", "");
		cacheKey = DigestUtils.md5DigestAsHex(cacheKey.getBytes());

		// Check if it supports gzip
		boolean supportsGzip = false;
		String enc = request.getHeader("Accept-Encoding");
		if (enc != null) {
			enc.replaceAll("\\s+", "").toLowerCase();
			supportsGzip = enc.indexOf("gzip") != -1 || request.getHeader("---------------") != null;
			enc = enc.indexOf("x-gzip") != -1 ? "x-gzip" : "gzip";
		}

		File cacheFile = null;
		if (supportsGzip) {
			cacheFile = new File(cachePath + File.separatorChar + "tiny_mce_" + cacheKey + ".gz");
		} else {
			cacheFile = new File(cachePath + File.separatorChar + "tiny_mce_" + cacheKey + ".js");
		}

		// Set the encoding once (for both cached and uncached requests)
		if (supportsGzip) {
			response.addHeader("Content-Encoding", enc);
		}

		// Use cached file disk cache
		if (cacheFile != null && cacheFile.exists()) {
			FileUtils.copyFile(cacheFile, outStream);
			return;
		}

		StringBuilder content = new StringBuilder("");

		// Add core
		content.append(getFileContents(getResource("tiny_mce.js")));
		// Patch loading functions
		content.append("tinyMCE_GZ.start();");

		// Add core languages
		for (String language : languages) {
			content.append(getFileContents(getResource("langs/" + language + ".js")));
		}

		loadThemes(languages, themes, content);
		loadPlugins(plugins, languages, content);

		// Restore loading functions
		content.append("tinyMCE_GZ.end();");

		// Generate GZIP'd content
		writeAndCache(outStream, supportsGzip, cacheFile, content);
	}

	private void writeAndCache(ServletOutputStream outStream, boolean supportsGzip, File cacheFile,
			StringBuilder content) throws IOException {
		if (supportsGzip) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			// Gzip compress
			GZIPOutputStream gzipStream = new GZIPOutputStream(bos);
			gzipStream.write(content.toString().getBytes("iso-8859-1"));
			gzipStream.close();

			// Write to cache
			LOGGER.debug("Writing cache to {}", cacheFile);
			FileUtils.writeByteArrayToFile(cacheFile, bos.toByteArray());
			// Write to stream
			IOUtils.write(bos.toByteArray(), outStream);
		} else {
			LOGGER.debug("Writing cache to {}", cacheFile);
			FileUtils.write(cacheFile, content);
			IOUtils.write(content, outStream);
		}
		cacheFile.deleteOnExit();
	}

	private void loadPlugins(String[] plugins, String[] languages, StringBuilder content) throws IOException {
		for (String plugin : plugins) {
			content.append(getFileContents(getResource("plugins/" + plugin + "/editor_plugin.js")));

			for (String language : languages) {
				content.append(getFileContents(getResource("plugins/" + plugin + "/langs/" + language + ".js")));
			}
		}
	}

	private void loadThemes(String[] languages, String[] themes, StringBuilder content) throws IOException {
		for (String theme : themes) {
			content.append(getFileContents(getResource("themes/" + theme + "/editor_template.js")));

			for (String language : languages) {
				content.append(getFileContents(getResource("themes/" + theme + "/langs/" + language + ".js")));
			}
		}
	}

	private String getParam(HttpServletRequest request, String name, String def) {
		String value = request.getParameter(name) != null ? "" + request.getParameter(name) : def;

		return value.replaceAll("[^0-9a-zA-Z\\-_,]+", "");
	}

	private String getFileContents(InputStream resource) throws IOException {
		if (resource == null) {
			// May happen for plugins without l10n
			return "";
		}
		String content = IOUtils.toString(resource);
		IOUtils.closeQuietly(resource);
		return content;
	}

	private InputStream getResource(String path) {
		InputStream resource = context.getResourceAsStream("/js/tiny_mce/" + path);
		if (resource == null) {
			LOGGER.trace("Failed to load {}", path);
		}
		return resource;
	}

}
