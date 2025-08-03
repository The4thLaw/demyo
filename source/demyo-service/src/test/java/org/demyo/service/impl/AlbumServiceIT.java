package org.demyo.service.impl;

import java.util.Date;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.demyo.model.Album;
import org.demyo.model.AlbumPrice;
import org.demyo.service.IAlbumService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AlbumService} and for the configuration of {@link Album} and {@link AlbumPrice}.
 */
@DatabaseSetup(value = "/org/demyo/test/demyo-dbunit-standard.xml", type = DatabaseOperation.REFRESH)
class AlbumServiceIT extends AbstractServiceTest {
	@Autowired
	private IAlbumService service;

	/**
	 * Ensures that prices can be loaded from the database in the correct order.
	 */
	@Test
	void testFindPrices() {
		long albumId = 313L;
		Album a = service.getByIdForEdition(albumId);
		assertThat(a.getPrices()).hasSize(2);
		Iterator<AlbumPrice> priceIterator = a.getPrices().iterator();
		AlbumPrice price1 = priceIterator.next();
		assertThat(price1.getAlbum().getId()).isEqualTo(albumId);
		assertThat(new Date(price1.getDate().getTime())).isEqualTo("2010-09-26");
		assertThat(price1.getPrice()).isEqualTo("15.0");
		AlbumPrice price2 = priceIterator.next();
		assertThat(price2.getAlbum().getId()).isEqualTo(albumId);
		assertThat(new Date(price2.getDate().getTime())).isEqualTo("2018-09-16");
		assertThat(price2.getPrice()).isEqualTo("22.5");
	}

	/**
	 * Tests {@link IAlbumService#countAlbumsByTaxon(long)}.
	 */
	@Test
	void testCountAlbumByFilter() {
		assertThat(service.countAlbumsByTaxon(5L)).isEqualTo(58);
	}
}
