package org.demyo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.model.Album;
import org.demyo.model.AlbumPrice;
import org.demyo.service.IAlbumService;

/**
 * Tests for {@link AlbumService} and for the configuration of {@link Album} and {@link AlbumPrice}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
public class AlbumServiceIT extends AbstractServiceTest {
	@Autowired
	private IAlbumService service;

	/**
	 * Ensures that prices can be loaded from the database in the correct order.
	 */
	@Test
	void testFindPrices() {
		Album a = service.getByIdForEdition(764);
		assertThat(a.getPrices()).hasSize(2);
		Iterator<AlbumPrice> priceIterator = a.getPrices().iterator();
		AlbumPrice price1 = priceIterator.next();
		assertThat(price1.getAlbum().getId()).isEqualTo(764);
		assertThat(new Date(price1.getDate().getTime())).isEqualTo("2010-09-26");
		assertThat(price1.getPrice()).isEqualTo("200.0");
		AlbumPrice price2 = priceIterator.next();
		assertThat(price2.getAlbum().getId()).isEqualTo(764);
		assertThat(new Date(price2.getDate().getTime())).isEqualTo("2011-09-26");
		assertThat(price2.getPrice()).isEqualTo("150.0");
	}

	/**
	 * Tests {@link IAlbumService#countAlbumsByTag(long)}.
	 */
	@Test
	void testCountAlbumByFilter() {
		assertThat(service.countAlbumsByTag(1L)).isEqualTo(24);
	}
}
