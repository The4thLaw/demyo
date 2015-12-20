package org.demyo.dao;

import org.demyo.model.Image;

import org.springframework.stereotype.Repository;

/**
 * This class provides methods to manipulate {@link Image}s.
 */
@Repository
public interface IImageRepo extends IModelRepo<Image> {
}
