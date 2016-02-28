package org.demyo.web.velocity.tools;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.ValidScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Velocity tool for the page quick tasks. This tool preserves the order in which the tasks are added.
 */
@ValidScope(value = Scope.REQUEST)
public class QuickTasksTool implements Iterable<QuickTasksTool.QuickTask> {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuickTasksTool.class);

	/**
	 * Describes a single quick tasks.
	 */
	public static class QuickTask {
		/** The HTML ID. */
		private String id;
		/** The icon. */
		private String icon;
		/** The URL to access (HTML 'href' attribute). */
		private String url;
		/** The label code to be passed to the message source. */
		private String label;
		/** The flag to determine whether clicking on this item requires confirmation. */
		private boolean confirm;

		/**
		 * Creates a quick task from a Map of its attributes.
		 * 
		 * @param properties The quick task attributes.
		 * @throws DemyoException If the quick task configuration is invalid.
		 */
		public QuickTask(Map<?, ?> properties) throws DemyoException {
			try {
				BeanUtils.populate(this, properties);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new DemyoException(DemyoErrorCode.QUICK_TASKS_INVALID_CONFIG, e);
			}

			// Automatically guess a label
			if (label == null) {
				if (id != null && id.startsWith("qt")) {
					label = id.replaceFirst("^qt-", "quickTasks.").replaceAll("-", ".");
				} else {
					throw new DemyoException(DemyoErrorCode.QUICK_TASKS_MISSING_LABEL);
				}
			}
		}

		/**
		 * Gets the HTML ID.
		 * 
		 * @return the HTML ID
		 */
		public String getId() {
			return id;
		}

		/**
		 * Sets the HTML ID.
		 * 
		 * @param id the new HTML ID
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * Gets the icon.
		 * 
		 * @return the icon
		 */
		public String getIcon() {
			return icon;
		}

		/**
		 * Sets the icon.
		 * 
		 * @param icon The icon specification.
		 */
		public void setIcon(String icon) {
			this.icon = icon;
		}

		/**
		 * Gets the URL to access (HTML 'href' attribute).
		 * 
		 * @return the URL to access (HTML 'href' attribute)
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * Sets the URL to access (HTML 'href' attribute).
		 * 
		 * @param url the new URL to access (HTML 'href' attribute)
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * Gets the label code to be passed to the message source.
		 * 
		 * @return the label code to be passed to the message source
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * Gets the label for confirmation.
		 * 
		 * @return the label
		 */
		public String getConfirmLabel() {
			return label + ".confirm";
		}

		/**
		 * Sets the label code to be passed to the message source.
		 * 
		 * @param label the new label code to be passed to the message source
		 */
		public void setLabel(String label) {
			this.label = label;
		}

		/**
		 * Checks if is the flag to determine whether clicking on this item requires confirmation.
		 * 
		 * @return the flag to determine whether clicking on this item requires confirmation
		 */
		public boolean isConfirm() {
			return confirm;
		}

		/**
		 * Sets the flag to determine whether clicking on this item requires confirmation.
		 * 
		 * @param confirm the new flag to determine whether clicking on this item requires confirmation
		 */
		public void setConfirm(boolean confirm) {
			this.confirm = confirm;
		}
	}

	private final List<QuickTask> tasks = new LinkedList<QuickTask>();

	/**
	 * Default constructor.
	 */
	public QuickTasksTool() {
		LOGGER.debug("Creating instance");
	}

	/**
	 * Adds a quick task.
	 * 
	 * @param config The quick task specification (see attributes of {@link QuickTask}).
	 * @throws DemyoException If the quick task configuration is invalid.
	 */
	public void add(Map<?, ?> config) throws DemyoException {
		LOGGER.trace("Registering quick task: {}", config);
		tasks.add(new QuickTask(config));
	}

	/**
	 * Prepares the quick tasks. Should be called before starting to render the page (and, in particular, before
	 * loading the Javascript).
	 * 
	 * @param js The {@link JavascriptTool} in the current request.
	 */
	public void prepare(JavascriptTool js) {
		if (tasks.isEmpty()) {
			return;
		}
		js.load("Demyo.QuickTasks");
	}

	@Override
	public Iterator<QuickTask> iterator() {
		return tasks.iterator();
	}

	public boolean isEmpty() {
		return tasks.isEmpty();
	}
}
