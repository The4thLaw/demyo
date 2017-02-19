package org.demyo.service;

import org.demyo.model.Derivative;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Order;

import com.querydsl.core.types.Predicate;

/**
 * Service for management of {@link Derivative}s.
 */
public interface IDerivativeService extends IModelService<Derivative> {

	Slice<Derivative> findPaginated(int currentPage, Predicate filter, Order... orders);

}
