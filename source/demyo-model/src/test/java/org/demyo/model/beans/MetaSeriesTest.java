package org.demyo.model.beans;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import org.demyo.model.Album;

/**
 * Tests from {@link MetaSeries}
 */
public class MetaSeriesTest {
	@Test
	public void compare() {
		LocaleContextHolder.setLocale(Locale.FRENCH);

		Album elephantAlb = new Album();
		elephantAlb.setTitle("Éléphant");

		Album fAlb = new Album();
		fAlb.setTitle("F");

		MetaSeries elephant = new MetaSeries(elephantAlb);
		MetaSeries f = new MetaSeries(fAlb);

		Assert.assertEquals(-1, elephant.compareTo(f));
	}
}
