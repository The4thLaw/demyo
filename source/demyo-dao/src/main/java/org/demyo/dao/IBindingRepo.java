package org.demyo.dao;

import org.demyo.model.Binding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Binding}s.
 */
@Repository
public interface IBindingRepo /*extends IModelRepo<Binding>*/extends JpaRepository<Binding, Long> {

}
