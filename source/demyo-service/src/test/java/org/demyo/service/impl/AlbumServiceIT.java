package org.demyo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Iterator;

import org.demyo.model.Album;
import org.demyo.model.AlbumPrice;
import org.demyo.service.IAlbumService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

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
	public void testFindPrices() {
		Album a = service.getByIdForEdition(1);
		assertThat(a.getPrices()).hasSize(2);
		Iterator<AlbumPrice> priceIterator = a.getPrices().iterator();
		AlbumPrice price1 = priceIterator.next();
		assertThat(price1.getAlbumId().getId()).isEqualTo(1);
		assertThat(new Date(price1.getDate().getTime())).isEqualTo("2016-01-10");
		assertThat(price1.getPrice()).isEqualTo("10.0");
		AlbumPrice price2 = priceIterator.next();
		assertThat(price2.getAlbumId().getId()).isEqualTo(1);
		assertThat(new Date(price2.getDate().getTime())).isEqualTo("2016-01-15");
		assertThat(price2.getPrice()).isEqualTo("1.5");
	}
}
