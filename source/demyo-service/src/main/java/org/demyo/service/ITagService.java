package org.demyo.service;

import org.demyo.model.Tag;
import org.demyo.service.impl.IQuickSearchableService;

/**
 * Service for management of {@link Tag}s.
 */
public interface ITagService extends IModelService<Tag>, IQuickSearchableService<Tag> {

}
