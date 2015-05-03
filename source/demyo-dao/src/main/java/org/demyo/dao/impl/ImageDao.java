package org.demyo.dao.impl;

import org.demyo.dao.IImageDao;
import org.demyo.model.Image;
import org.springframework.stereotype.Repository;

/**
 * Implements the contract defined by {@link IImageDao}.
 * 
 * @author $Author: xr $
 * @version $Revision: 1030 $
 */
@Repository
public class ImageDao extends AbstractModelDao<Image> implements IImageDao {

	/**
	 * Default constructor.
	 */
	protected ImageDao() {
		super(Image.class);
	}
}
