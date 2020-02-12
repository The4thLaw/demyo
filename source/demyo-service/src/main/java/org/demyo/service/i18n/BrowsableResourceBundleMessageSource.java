package org.demyo.service.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * A {@link ResourceBundleMessageSource} that can list all messages it knows.
 */
public class BrowsableResourceBundleMessageSource extends ResourceBundleMessageSource {
	/**
	 * Gets all messages known by this message source.
	 * <p>
	 * Does <strong>not</strong> cache the results.</p<
	 * 
	 * @param locale The locale to get messages for.
	 * @return The messages, indexed by message key.
	 */
	public Map<String, String> getAllMessages(Locale locale) {
		Map<String, String> messages = new HashMap<>();
		for (String basename : getBasenameSet()) {
			ResourceBundle bundle = doGetBundle(basename, locale);
			for (String key : bundle.keySet()) {
				messages.put(key, bundle.getString(key));
			}
		}
		return messages;
	}
}
