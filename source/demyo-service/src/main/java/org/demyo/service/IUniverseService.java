package org.demyo.service;

import java.util.List;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Album;
import org.demyo.model.Universe;

/**
 * Service for management of {@link Univers}s.
 */
public interface IUniverseService extends IModelService<Universe>, IQuickSearchableService<Universe> {
	/**
	 * Finds the Albums which are part of a Universe, either directly or through its Series.
	 * @param id The Universe ID
	 * @return The found Albums
	 */
    List<Album> getContents(long id);

	/**
	 * Recovers images from FilePond and uses them for the specified Universe.
	 * <p>
	 * The logo replaces any existing one. The other images are added to the current ones.
	 * </p>
	 *
	 * @param universeId The ID of the Universe to change.
	 * @param logoFilePondId The FilePond ID of the logo to recover.
	 * @param otherFilePondIds The FilePond IDs of the other images to recover.
	 * @throws DemyoException In case of error during recovery.
	 */
	void recoverFromFilePond(long universeId, String logoFilePondId, String[] otherFilePondIds) throws DemyoException;
}
