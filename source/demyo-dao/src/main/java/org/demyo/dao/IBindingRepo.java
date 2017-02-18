package org.demyo.dao;

import org.demyo.model.Binding;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Binding}s.
 */
@Repository
public interface IBindingRepo extends IModelRepo<Binding> {
	@Query("select x from #{#entityName} x where id=?1")
	@EntityGraph("Binding.forView")
	Binding findOneForView(long id);
}
