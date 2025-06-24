package org.demyo.service;

import java.util.List;

import org.demyo.model.Album;
import org.demyo.model.Universe;

/**
 * Service for management of {@link Univers}s.
 */
public interface IUniverseService extends IModelService<Universe> {
	/**
	 * Finds the Albums which are part of a Universe, either directly or through its Series.
	 * @param id The Universe ID
	 * @return The found Albums
	 */
    List<Album> getContents(long id);
}
