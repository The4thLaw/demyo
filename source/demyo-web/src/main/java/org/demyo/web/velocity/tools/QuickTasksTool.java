package org.demyo.web.velocity.tools;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.demyo.model.exception.DemyoErrorCode;
import org.demyo.model.exception.DemyoException;
import org.demyo.service.ITranslationService;
import org.demyo.utils.web.SpringContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.ValidScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Velocity tool for the page quick tasks. This tool preserves the order in which the tasks are added.
 * 
 * @author $Author: xr $
 * @version $Revision: 1074 $
 */
@ValidScope(value = Scope.REQUEST)
public class QuickTasksTool {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuickTasksTool.class);
	private static final String QUICK_TASKS_TITLE_CODE = "quickTasks.title";

	/**
	 * Describes a single quick tasks.
	 * 
	 * @author $Author: xr $
	 * @version $Revision: 1074 $
	 */
	public static class QuickTask {
		/** The HTML ID. */
		private String id;
		/** The CSS class or classes. */
		private String css = "";
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

			// Automatically guess a class
			if (css.isEmpty() && id != null) {
				if (id.startsWith("qt-add")) {
					css = "add";
				} else if (id.startsWith("qt-edit")) {
					css = "edit";
				} else if (id.startsWith("qt-delete")) {
					css = "delete";
				}
			}
		}

		/**
		 * Generates an HTML snippet for the current task.
		 * 
		 * @param sb The {@link StringBuilder} to append the HTML to.
		 * @param ts The translation service, for labels.
		 */
		public void toHTML(StringBuilder sb, ITranslationService ts) {
			sb.append("\n\t<li");
			appendAttribute(sb, "id", id);
			appendAttribute(sb, "class", css);
			if (confirm) {
				appendAttribute(sb, "data-qt-confirm", ts.translate(label + ".confirm"));
			}
			sb.append(">");
			if (url != null) {
				sb.append("<a href=\"").append(url).append("\">");
			}
			sb.append(ts.translate(label));
			if (url != null) {
				sb.append("</a>");
			}
			sb.append("</li>");
		}

		private static void appendAttribute(StringBuilder sb, String attrName, String attrValue) {
			if (StringUtils.isNotEmpty(attrValue)) {
				sb.append(" ").append(attrName).append("=\"").append(attrValue).append("\"");
			}
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
		 * Sets the CSS class or classes.
		 * 
		 * @param css the new CSS class or classes
		 */
		public void setCss(String css) {
			this.css = css;
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
		 * Sets the label code to be passed to the message source.
		 * 
		 * @param label the new label code to be passed to the message source
		 */
		public void setLabel(String label) {
			this.label = label;
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
	private final ITranslationService translationService;

	/**
	 * Default constructor.
	 */
	public QuickTasksTool() {
		LOGGER.debug("Creating instance");
		translationService = SpringContext.getContext().getBean(ITranslationService.class);
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

	/**
	 * Returns the HTML to display the list of quick tasks.
	 * 
	 * @return The HTML snippet.
	 */
	public String list() {
		if (tasks.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder("<ul id=\"quick-tasks\" data-title=\"");
		sb.append(translationService.translate(QUICK_TASKS_TITLE_CODE)).append("\">");

		for (QuickTask task : tasks) {
			task.toHTML(sb, translationService);
		}

		sb.append("\n</ul>");

		return sb.toString();
	}
}
