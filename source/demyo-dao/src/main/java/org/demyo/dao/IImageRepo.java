package org.demyo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.demyo.model.Image;

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

	/**
	 * Returns a model for the view page.
	 * 
	 * @param id The identifier of the model.
	 * @return The fetched model.
	 */
	@Query("select x from #{#entityName} x where id=?1")
	Image findOneForView(long id);

	/**
	 * Returns a model for the list of entities depending on this image.
	 * 
	 * @param id The identifier of the model.
	 * @return The fetched model.
	 */
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Image.forDependencies")
	Image findOneForDependencies(long id);

	/**
	 * Finds all Images whose description matches the provided pattern.
	 * 
	 * @param pattern The SQL pattern to match.
	 * @return The images.
	 */
	List<Image> findByDescriptionLike(String pattern);
}
