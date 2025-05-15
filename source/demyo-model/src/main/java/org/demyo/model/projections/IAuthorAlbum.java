package org.demyo.model.projections;

/**
 * Structures a set of Album identifiers based on the role of the Author who worked on them.
 */
public interface IAuthorAlbum {
	/**
	 * Gets the Author ID.
	 *
	 * @return The ID
	 */
	Long getAuthorId();

	/**
	 * Gets the Album ID for which the Author is an artist.
	 *
	 * @return The ID
	 */
	Long getAsArtist();

	/**
	 * Gets the Album ID for which the Author is a colorist.
	 *
	 * @return The ID
	 */
	Long getAsColorist();

	/**
	 * Gets the Album ID for which the Author is a cover artist.
	 *
	 * @return The ID
	 */
	Long getAsCoverArtist();

	/**
	 * Gets the Album ID for which the Author is an inker.
	 *
	 * @return The ID
	 */
	Long getAsInker();

	/**
	 * Gets the Album ID for which the Author is a translator.
	 *
	 * @return The ID
	 */
	Long getAsTranslator();

	/**
	 * Gets the Album ID for which the Author is a writer.
	 *
	 * @return The ID
	 */
	Long getAsWriter();
}
