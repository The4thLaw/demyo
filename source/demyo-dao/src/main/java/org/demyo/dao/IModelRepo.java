package org.demyo.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import org.demyo.model.IModel;

/**
 * Base interface for manipulation of models. This doesn't inherit from JpaRepository, in order to be free to define
 * exactly what we need.
 * 
 * @param <M> The model type (an {@link IModel}).
 */
@NoRepositoryBean
public interface IModelRepo<M extends IModel> extends JpaRepository<M, Long>, QuerydslPredicateExecutor<M> {
	/**
	 * Returns a model for edition. Repositories needing to fetch more entities for edition should override this method
	 * and specify an {@link EntityGraph}.
	 * 
	 * @param id The identifier of the model.
	 * @return The fetched model.
	 */
	@Query("select x from #{#entityName} x where id=?1")
	M findOneForEdition(long id);

	/**
	 * Returns a list of entities in the requested order.
	 * 
	 * @param sort The order.
	 * @return a list of entities
	 */
	@Override
	List<M> findAll(Sort sort);
}
