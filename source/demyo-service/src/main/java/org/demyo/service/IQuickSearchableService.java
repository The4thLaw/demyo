package org.demyo.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.demyo.model.IModel;

/**
 * Marker interface for services supporting quick search.
 * 
 * @param <M> The model type.
 */
public interface IQuickSearchableService<M extends IModel> {
	CompletableFuture<List<M>> quickSearch(String query, boolean exact);
}
