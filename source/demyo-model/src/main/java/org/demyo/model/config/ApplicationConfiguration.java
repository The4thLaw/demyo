package org.demyo.model.config;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import org.demyo.common.config.SystemConfiguration;

/**
 * Demyo application configuration. These are things that the user can change through the application and impact its
 * visible behaviour.
 * 
 * @see SystemConfiguration
 */
public class ApplicationConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

	/**
	 * The system locale.
	 * <p>
	 * This is the default locale used when Demyo was started, before Demyo made any change.
	 * </p>
	 */
	public static final Locale SYSTEM_LOCALE = Locale.getDefault();
	/**
	 * The configuration key corresponding to the language.
	 */
	public static final String CONFIG_KEY_LANGUAGE = "language";
	/**
	 * The configuration key corresponding to the currency.
	 */
	private static final String CONFIG_KEY_CURRENCY = "currency";
	private static final HashSet<String> GLOBAL_ENTRIES = new HashSet<>();

	static {
		GLOBAL_ENTRIES.add(CONFIG_KEY_CURRENCY);
	}

	/**
	 * Gets a default configuration with default values.
	 * 
	 * @return the default configuration.
	 */
	public static ApplicationConfiguration getDefaultConfiguration() {
		ApplicationConfiguration config = new ApplicationConfiguration();

		// The default values for the configuration are managed on the client side. Let's just set the
		config.setLanguage(SYSTEM_LOCALE);

		return config;
	}

	/**
	 * Checks if the provided configuration key is a global setting that affects all readers.
	 * 
	 * @param key The configuration key to check.
	 * @return <code>true</code> if this configuration is global.
	 */
	public static boolean isGlobalEntry(String key) {
		return GLOBAL_ENTRIES.contains(key);
	}

	/** The language in which the application is displayed. */
	private Locale language;
	/** The currency used for the application. */
	private String currency;
	/** The number of items per page of textual entries. */
	private Integer pageSizeForText;
	/** The number of items per page of images. */
	private Integer pageSizeForImages;
	/** The number of items per page of cards. */
	private Integer pageSizeForCards;
	/** The number of items in a card. */
	private Integer subItemsInCardIndex;

	/**
	 * Default constructor with no values.
	 */
	private ApplicationConfiguration() {
	}

	/**
	 * Creates an application configuration from a configuration file.
	 * 
	 * @param config The configuration to retrieve values from.
	 */
	public ApplicationConfiguration(Map<String, String> config) {
		merge(config);
	}

	/**
	 * Merges the current application configuration with the provided one.
	 * 
	 * @param config The configuration to retrieve values from.
	 */
	public void merge(Map<String, String> config) {
		String languageCode = getString(config, CONFIG_KEY_LANGUAGE);
		Locale theLanguage;
		if (languageCode != null) {
			theLanguage = Locale.forLanguageTag(languageCode);
		} else {
			theLanguage = Locale.getDefault();
		}
		setLanguage(theLanguage);

		currency = getString(config, CONFIG_KEY_CURRENCY);
		pageSizeForText = getInt(config, "paging.textPageSize");
		pageSizeForImages = getInt(config, "paging.imagePageSize");
		pageSizeForCards = getInt(config, "paging.pageSizeForCards");
		subItemsInCardIndex = getInt(config, "paging.subItemsInCardIndex");
	}

	private static String getString(Map<String, String> config, String key) {
		return getString(config, key, null);
	}

	private static String getString(Map<String, String> config, String key, String defaultValue) {
		String value = config.get(key);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	private static Integer getInt(Map<String, String> config, String key) {
		String value = config.get(key);
		return StringUtils.isNotBlank(value) ? Integer.valueOf(value) : null;
	}

	/**
	 * Returns a map of all values as strings.
	 * 
	 * @return The map.
	 */
	public SortedMap<String, String> asMap() {
		SortedMap<String, String> config = new TreeMap<>();

		config.put(CONFIG_KEY_LANGUAGE, language.toLanguageTag());
		config.put(CONFIG_KEY_CURRENCY, currency);
		setIfNotNull(config, "paging.textPageSize", pageSizeForText);
		setIfNotNull(config, "paging.imagePageSize", pageSizeForImages);
		setIfNotNull(config, "paging.pageSizeForCards", pageSizeForCards);
		setIfNotNull(config, "paging.subItemsInCardIndex", subItemsInCardIndex);

		LOGGER.debug("Configuration as map: {}", config);

		return config;
	}

	private static void setIfNotNull(SortedMap<String, String> config, String key, Integer value) {
		if (value != null) {
			config.put(key, String.valueOf(value));
		}
	}

	/**
	 * Gets the language in which the application is displayed.
	 * 
	 * @return the language in which the application is displayed
	 */
	public Locale getLanguage() {
		return language;
	}

	/**
	 * Sets the language in which the application is displayed.
	 * 
	 * @param language the new language in which the application is displayed
	 */
	public void setLanguage(Locale language) {
		this.language = language;

		// This won't impact the JVM locale (used notably for javax.validation messages), but we can't really do that
		// in a multi-reader context (this would result in an edit war between readers).
		LocaleContextHolder.setLocale(language);
	}

	/**
	 * Gets the currency used for the application.
	 *
	 * @return the currency used for the application
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * Sets the currency used for the application.
	 *
	 * @param currency the new currency used for the application
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * Gets the number of items per page of textual entries.
	 * 
	 * @return the number of items per page of textual entries
	 */
	public Integer getPageSizeForText() {
		return pageSizeForText;
	}

	/**
	 * Gets the number of items per page of images.
	 * 
	 * @return the number of items per page of images
	 */
	public Integer getPageSizeForImages() {
		return pageSizeForImages;
	}

	/**
	 * Gets the number of items per page of cards.
	 *
	 * @return the number of items per page of cards
	 */
	public Integer getPageSizeForCards() {
		return pageSizeForCards;
	}

	/**
	 * Gets the number of items in a card.
	 *
	 * @return the number of items in a card
	 */
	public Integer getSubItemsInCardIndex() {
		return subItemsInCardIndex;
	}

}
