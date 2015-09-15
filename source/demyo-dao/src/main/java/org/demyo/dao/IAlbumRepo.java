package org.demyo.dao;

import org.demyo.model.Album;

import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Album}s.
 */
@Repository
public interface IAlbumRepo extends IModelRepo<Album> {

	// Order by COALESCE?
}
