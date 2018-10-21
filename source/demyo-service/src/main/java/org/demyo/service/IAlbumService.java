package org.demyo.service;

import java.util.List;

import org.demyo.common.exception.DemyoException;
import org.demyo.model.Album;
import org.demyo.model.Series;

/**
 * Service for management of {@link Album}s.
 */
public interface IAlbumService extends IModelService<Album>, IQuickSearchableService<Album> {

	/**
	 * Finds a template {@link Album} for a given {@link Series}, with the aim of adding a new entry. The retrieved
	 * {@link Album} is the last one from the {@link Series}.
	 * 
	 * @param seriesId The {@link Series} to query.
	 * @return The template {@link Album}.
	 */
	Album getAlbumTemplateForSeries(long seriesId);

	/**
	 * Finds the {@link Album}s belonging to a specific {@link Series}.
	 * 
	 * @param seriesId The Series ID. Can be <code>null</code> to find Albums without Series
	 * @return The associated Albums
	 */
	List<Album> findBySeriesId(Long seriesId);

	/**
	 * Recovers images from FilePond and uses them for the specified Album.
	 * <p>
	 * The cover replaces any existing one. The other images are added to the current one.
	 * </p>
	 * 
	 * @param albumId The ID of the Album to change.
	 * @param coverFilePondId The FilePond ID of the cover to recover.
	 * @param otherFilePondIds The FilePond IDs of the other images to recover.
	 * @throws DemyoException In case of error during recovery.
	 */
	void recoverFromFilePond(long albumId, String coverFilePondId, String[] otherFilePondIds) throws DemyoException;

}
