package org.demyo.web.velocity.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.demyo.service.impl.TranslationService;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.ValidScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Velocity tool for Rich Text Editing.
 * <p>
 * Replacements based on description at http://stackoverflow.com/a/377484
 * </p>
 */
@ValidScope(value = Scope.APPLICATION)
public class RteTool {
	private static final Logger LOGGER = LoggerFactory.getLogger(RteTool.class);

	private static final String PATTERN_MODEL_LINKS = "href=\"((Album|Author|Binding|Collection|Derivative"
			+ "|DerivativeType|Publisher|Series|Source):([0-9]+))\"";
	private static final int GROUP_MODEL_LINKS_MODEL = 2;
	private static final int GROUP_MODEL_LINKS_ID = 3;

	// Maybe extract this to an inflector if ever needed. Consider https://issues.apache.org/jira/browse/LANG-485 in that case
	private static final Map<String, String> MODEL_TO_CONTROLLER = new HashMap<String, String>();

	static {
		MODEL_TO_CONTROLLER.put("Album", "albums");
		MODEL_TO_CONTROLLER.put("Author", "authors");
		MODEL_TO_CONTROLLER.put("Binding", "bindings");
		MODEL_TO_CONTROLLER.put("Collection", "collections");
		MODEL_TO_CONTROLLER.put("Derivative", "derivatives");
		MODEL_TO_CONTROLLER.put("DerivativeType", "derivativeTypes");
		MODEL_TO_CONTROLLER.put("Publisher", "publishers");
		MODEL_TO_CONTROLLER.put("Series", "series");
		MODEL_TO_CONTROLLER.put("Source", "sources");
	}

	/**
	 * Default constructor.
	 */
	public RteTool() {
		LOGGER.debug("Creating instance");
	}

	/**
	 * Pre-processes all text to provide an additional layer of convenience to the rich text fields.
	 * 
	 * @param text The text to parse.
	 * @param contextRoot The context root, used to resolve links.
	 * @param translationService The translation service.
	 * @return The filtered text.
	 * @see #replaceModelLinks(CharSequence, String)
	 * @see #replaceOrdinals(CharSequence, TranslationService)
	 */
	public String parse(String text, String contextRoot, TranslationService translationService) {
		if (text == null) {
			return null;
		}

		CharSequence replaced = replaceModelLinks(text, contextRoot);
		replaced = replaceOrdinals(replaced, translationService);

		return replaced.toString();
	}

	/**
	 * Replaces models links like "Author:2" to actual controller references.
	 * 
	 * @param text The text to parse.
	 * @param contextRoot The context root, used to resolve links.
	 * @return The filtered text.
	 */
	private CharSequence replaceModelLinks(CharSequence text, String contextRoot) {
		StringBuffer resultString = new StringBuffer();
		Pattern regex = Pattern.compile(PATTERN_MODEL_LINKS);
		Matcher regexMatcher = regex.matcher(text);
		while (regexMatcher.find()) {
			MatchResult result = regexMatcher.toMatchResult();
			String model = result.group(GROUP_MODEL_LINKS_MODEL);
			String controller = MODEL_TO_CONTROLLER.get(model);
			if (controller == null) {
				controller = model; // fallback
			}
			String id = result.group(GROUP_MODEL_LINKS_ID);
			regexMatcher.appendReplacement(resultString, "href=\"" + contextRoot + "/" + controller + "/view/"
					+ id + "\"");
		}
		regexMatcher.appendTail(resultString);
		return resultString;
	}

	/**
	 * Replaces ordinal markers (1st, 2nd) with HTML equivalents using superscript.
	 * 
	 * @param text The text to parse.
	 * @param translationService The translation service.
	 * @return The filtered text.
	 */
	private CharSequence replaceOrdinals(CharSequence text, TranslationService translationService) {
		StringBuffer resultString = new StringBuffer();
		Pattern regex = Pattern.compile(translationService.translate("special.ordinal_regex"));
		Matcher regexMatcher = regex.matcher(text);
		while (regexMatcher.find()) {
			MatchResult result = regexMatcher.toMatchResult();
			regexMatcher.appendReplacement(resultString, "<sup>" + result.group() + "</sup>");
		}
		regexMatcher.appendTail(resultString);

		return resultString;
	}
}
