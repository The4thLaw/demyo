package org.demyo.service.impl;

import java.util.Locale;

import org.demyo.service.ITranslationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

/**
 * Implements the contract defined by {@link ITranslationService}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1 $
 */
@Service
public class TranslationService implements ITranslationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TranslationService.class);
	private static final Object[] NO_PARAMS = new Object[0];
	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	@Autowired
	private ConfigurationService configService;

	@Autowired
	private MessageSource messageSource;

	@Override
	public String translate(String labelId) {
		return translate(labelId, NO_PARAMS);
	}

	@Override
	public String translate(String labelId, Object[] params) {
		Locale locale = getLocale();
		try {
			return messageSource.getMessage(labelId, params, locale);
		} catch (NoSuchMessageException e1) {
			LOGGER.debug("No translation for {} in {}, trying fallback", labelId, locale);
			try {
				return messageSource.getMessage(labelId, params, DEFAULT_LOCALE);
			} catch (NoSuchMessageException e2) {
				LOGGER.warn("Missing translation for {}", labelId);
				return labelId + " (" + locale + ")";
			}
		}
	}

	private Locale getLocale() {
		return configService.getConfiguration().getLanguage();
	}
}
