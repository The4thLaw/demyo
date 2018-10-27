package org.demyo.service;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Derivative;

/**
 * Service for management of {@link Derivative}s.
 */
public interface IDerivativeService extends IModelService<Derivative> {
	/**
	 * Recovers images from FilePond and uses them for the specified Derivative.
	 * <p>
	 * The other images are added to the current ones.
	 * </p>
	 * 
	 * @param derivativeId The ID of the Album to change.
	 * @param otherFilePondIds The FilePond IDs of the other images to recover.
	 * @throws DemyoException In case of error during recovery.
	 */
	void recoverFromFilePond(long derivativeId, String[] otherFilePondIds) throws DemyoException;
}
