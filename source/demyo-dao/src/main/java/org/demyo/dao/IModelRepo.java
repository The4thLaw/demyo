package org.demyo.dao;

import org.demyo.model.IModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base interface for manipulation of models.
 * 
 * @param <M> The model type (an {@link IModel}).
 */
@NoRepositoryBean
public interface IModelRepo<M extends IModel> extends JpaRepository<M, Long> {

}
