package org.demyo.service.impl;

import java.util.List;
import java.util.concurrent.Future;

import org.demyo.model.IModel;

/**
 * Marker interface for services supporting quick search.
 * 
 * @param <M> The model type.
 */
public interface IQuickSearchableService<M extends IModel> {
	Future<List<M>> quickSearch(String query, boolean exact);
}
