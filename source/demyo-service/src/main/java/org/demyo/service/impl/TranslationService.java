package org.demyo.service.impl;

import java.util.ArrayList;
import java.util.Locale;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.service.IConfigurationService;
import org.demyo.service.IReaderService;
import org.demyo.service.ITranslationService;

/**
 * Implements the contract defined by {@link ITranslationService}.
 */
@Service
public class TranslationService implements ITranslationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TranslationService.class);
	private static final Object[] NO_PARAMS = new Object[0];
	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	@Autowired
	private IReaderService readerService;
	@Autowired
	private IConfigurationService configService;
	@Autowired
	private MessageSource messageSource;

	@Override
	public String translate(@NotEmpty String labelId) {
		return translate(labelId, NO_PARAMS);
	}

	@Override
	public String translate(@NotEmpty String labelId, Object[] params) {
		if (params.length == 1 && params[0] instanceof ArrayList<?>) {
			// Unbox ArrayList provided by velocity
			params = ((ArrayList<?>) params[0]).toArray();
		}

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

	@Override
	public String translateVargs(@NotEmpty String labelId, Object... params) {
		return translate(labelId, params);
	}

	private Locale getLocale() {
		try {
			return readerService.getContext().getConfiguration().getLanguage();
		} catch (RuntimeException re) {
			LOGGER.debug("No locale available in the reader context");
			// It could be that we don't have a reader context or something like that. This will definitely happen
			// for desktop integration, but could also happen in background tasks. In that case:
			// 1. Try to get the language of the first reader, if any
			Locale firstReaderLocale = configService.getLocaleForFirstReader();
			if (firstReaderLocale != null) {
				LOGGER.debug("The first reader has a locale of {}, using it", firstReaderLocale.toLanguageTag());
				return firstReaderLocale;
			}
			// 2. Use the system language
			LOGGER.debug("No locale available for any reader, using the default system locale: {}",
					ApplicationConfiguration.SYSTEM_LOCALE);
			return ApplicationConfiguration.SYSTEM_LOCALE;
		}

	}
}
