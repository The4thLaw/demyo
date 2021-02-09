package org.demyo.model.beans;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import org.demyo.model.Album;

/**
 * Tests from {@link MetaSeries}
 */
class MetaSeriesTest {
	@Test
	void compare() {
		LocaleContextHolder.setLocale(Locale.FRENCH);

		Album elephantAlb = new Album();
		elephantAlb.setTitle("Éléphant");

		Album fAlb = new Album();
		fAlb.setTitle("F");

		MetaSeries elephant = new MetaSeries(elephantAlb);
		MetaSeries f = new MetaSeries(fAlb);

		assertThat(elephant.compareTo(f)).isNegative();
	}
}
