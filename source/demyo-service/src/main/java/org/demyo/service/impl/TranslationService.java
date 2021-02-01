package org.demyo.service.impl;

import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.model.config.ApplicationConfiguration;
import org.demyo.service.IConfigurationService;
import org.demyo.service.ITranslationService;
import org.demyo.service.i18n.BrowsableResourceBundleMessageSource;

/**
 * Implements the contract defined by {@link ITranslationService}.
 */
@Service
public class TranslationService implements ITranslationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TranslationService.class);
	private static final Object[] NO_PARAMS = new Object[0];
	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	@Autowired
	private IConfigurationService configService;
	@Autowired
	private MessageSource messageSource;

	@Cacheable(cacheNames = "ReferenceData")
	@Override
	public Map<String, String> getAllTranslations(String locale) {
		if (!(messageSource instanceof BrowsableResourceBundleMessageSource)) {
			throw new DemyoRuntimeException(DemyoErrorCode.SYS_SPRING_CONFIG_INCONSISTENT,
					"Unexpected MessageSource class: " + messageSource.getClass());
		}
		LOGGER.debug("Detecting translations for all messages of locale {}", locale);
		return ((BrowsableResourceBundleMessageSource) messageSource)//
				.getAllMessages(Locale.forLanguageTag(locale));
	}

	@Override
	public String translate(@NotEmpty String labelId) {
		return translate(labelId, NO_PARAMS);
	}

	@Override
	public String translate(@NotEmpty String labelId, Object[] params) {
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
		// Here we don't have a reader context or something like that. This will definitely happen
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
