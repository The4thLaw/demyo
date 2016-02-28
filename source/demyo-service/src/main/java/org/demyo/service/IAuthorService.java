package org.demyo.service;

import org.demyo.model.Author;
import org.demyo.service.impl.IQuickSearchableService;

/**
 * Service for management of {@link Author}s.
 */
public interface IAuthorService extends IModelService<Author>, IQuickSearchableService<Author> {

}
