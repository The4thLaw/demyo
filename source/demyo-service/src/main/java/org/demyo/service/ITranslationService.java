package org.demyo.service;

import java.util.Map;

public interface ITranslationService {

	String translate(String labelId);

	String translate(String labelId, Object[] params);

	String translateVargs(String labelId, Object... params);

	/**
	 * Gets all translations available for a given language.
	 * 
	 * @param locale The language to process.
	 * @return The translations
	 */
	Map<String, String> getAllTranslations(String locale);

}
