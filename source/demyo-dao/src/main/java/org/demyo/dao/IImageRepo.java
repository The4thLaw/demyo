package org.demyo.dao;

import org.demyo.model.Image;

import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Image}s.
 */
@Repository
public interface IImageRepo extends IModelRepo<Image>, IImageCustomRepo {
	/**
	 * Finds an {@link Image} based on its path.
	 * 
	 * @param url The path to the {@link Image}.
	 * @return The found {@link Image}.
	 */
	Image findByUrl(String url);
}
