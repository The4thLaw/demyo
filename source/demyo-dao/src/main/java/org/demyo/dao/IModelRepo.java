package org.demyo.dao;

import java.util.List;

import org.demyo.model.IModel;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
	@Override
	List<M> findAll();

	/**
	 * Returns a {@link Slice} of entities meeting the paging restriction provided in the {@code Pageable} object.
	 * 
	 * @param pageable
	 * @return a page of entities
	 */
	Slice<M> findAll(Pageable pageable);
}
