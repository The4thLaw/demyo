package org.demyo.web.velocity.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.ValidScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Velocity tool for Javascript management.
 */
@ValidScope(value = Scope.REQUEST)
public class JavascriptTool {
	private static final Logger LOGGER = LoggerFactory.getLogger(JavascriptTool.class);

	/**
	 * This class represents a Javascript file: its location, dependencies and potential CSS annex.
	 */
	private static class Script {
		private final String id;
		private final String file;
		private final Script[] dependencies;
		private final String css;

		public Script(String id, String file, Script... dependencies) {
			this(id, file, null, dependencies);
		}

		public Script(String id, String file, String css, Script... dependencies) {
			this.id = id;
			this.file = file;
			this.css = css;
			this.dependencies = dependencies;
		}

		public String getId() {
			return id;
		}

		public String getFile() {
			return file;
		}

		public Script[] getDependencies() {
			return dependencies;
		}

		public String getCss() {
			return css;
		}
	}

	private static final Map<String, Script> KNOWN_SCRIPTS;

	static {
		KNOWN_SCRIPTS = new HashMap<String, Script>();

		addScript(new Script("TinyMCE", "vendor/tinymce/tinymce.min.js"));
		addScript(new Script("Dialog-Polyfill", "vendor/dialog-polyfill.js"));
		addScript(new Script("Material", "vendor/material.min.js", KNOWN_SCRIPTS.get("Dialog-Polyfill")));
		addScript(new Script("PrefixFree", "vendor/prefixfree.min.js"));
		addScript(new Script("JQuery", "vendor/jquery-1.9.1.js"));
		addScript(new Script("Demyo.Core", "demyo_core.js", KNOWN_SCRIPTS.get("JQuery"),
				KNOWN_SCRIPTS.get("Material"), KNOWN_SCRIPTS.get("PrefixFree")));
		addScript(new Script("Chosen", "vendor/chosen.jquery.min.js", KNOWN_SCRIPTS.get("JQuery")));
		addScript(new Script("Demyo.Forms", "demyo_forms.js", KNOWN_SCRIPTS.get("JQuery"),
				KNOWN_SCRIPTS.get("Chosen"), KNOWN_SCRIPTS.get("Demyo.Core")));
		addScript(new Script("JQuery.HotKeys", "vendor/jquery.hotkeys.js", KNOWN_SCRIPTS.get("JQuery")));
		addScript(new Script("Demyo.QuickTasks", "demyo_quicktasks.js", KNOWN_SCRIPTS.get("JQuery")));
		addScript(new Script("Demyo.QuickSearch", "demyo_quicksearch.js", KNOWN_SCRIPTS.get("JQuery"),
				KNOWN_SCRIPTS.get("Demyo.Core")));
	}

	private final Set<String> loadedScripts = new LinkedHashSet<String>();

	/**
	 * Default constructor.
	 */
	public JavascriptTool() {
		LOGGER.debug("Creating instance");
	}

	private static void addScript(Script s) {
		KNOWN_SCRIPTS.put(s.getId(), s);
	}

	/**
	 * Registers a script as needing to be loaded in the page.
	 * 
	 * @param id The ID of the script in the registry.
	 */
	public void load(String id) {
		if (isLoaded(id)) {
			LOGGER.trace("Script {} is already loaded", id);
			return;
		}

		Script s = KNOWN_SCRIPTS.get(id);
		if (s == null) {
			throw new IllegalArgumentException("No script with id '" + id + "'");
		}

		for (Script dep : s.getDependencies()) {
			load(dep.getId());
		}

		LOGGER.debug("Loading script {}", id);
		loadedScripts.add(id);
	}

	/**
	 * Checks whether a script has been loaded.
	 * 
	 * @param id The ID of the script in the registry.
	 * @return <code>true</code> if the script is loaded. <code>false</code> if it's not, or if it doesn't exist.
	 */
	public boolean isLoaded(String id) {
		return loadedScripts.contains(id);
	}

	/**
	 * Gets a list of all script source locations, relative to the Javascript directory.
	 * 
	 * @return The script source locations.
	 */
	public List<String> getScriptSources() {
		ArrayList<String> list = new ArrayList<String>(loadedScripts.size());
		for (String id : loadedScripts) {
			list.add(KNOWN_SCRIPTS.get(id).getFile());
		}
		return list;
	}

	/**
	 * Gets a list of all script CSS annex locations, relative to the css directory.
	 * 
	 * @return The CSS annex locations.
	 */
	public List<String> getCssDependencies() {
		ArrayList<String> list = new ArrayList<String>(loadedScripts.size());
		for (String id : loadedScripts) {
			String css = KNOWN_SCRIPTS.get(id).getCss();
			if (css != null) {
				list.add(css);
			}
		}
		return list;
	}
}
