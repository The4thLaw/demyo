package org.demyo.dao;

import java.util.List;

import org.demyo.model.IModel;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base interface for manipulation of models. This doesn't inherit from JpaRepository, in order to be free to
 * define exactly what we need.
 * 
 * @param <M> The model type (an {@link IModel}).
 */
@NoRepositoryBean
public interface IModelRepo<M extends IModel> extends CrudRepository<M, Long> {
	/**
	 * Returns a model for edition. Repositories needing to fetch more entities for edition should override this
	 * method and specify an {@link EntityGraph}.
	 * 
	 * @param id The identifier of the model.
	 * @return The fetched model.
	 */
	@Query("select x from #{#entityName} x where id=?1")
	public M findOneForEdition(long id);

	/**
	 * Returns a list of entities in the requested order.
	 * 
	 * @param sort The order.
	 * @return a list of entities
	 */
	List<M> findAll(Sort sort);

	/**
	 * Returns a {@link Slice} of entities meeting the paging restriction provided in the {@code Pageable} object.
	 * 
	 * @param pageable
	 * @return a page of entities
	 */
	Slice<M> findAll(Pageable pageable);
}
