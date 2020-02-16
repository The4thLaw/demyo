package org.demyo.model.projections;

import org.springframework.beans.factory.annotation.Value;

/**
 * Structures a set of Album identifiers based on the role of the Author who worked on them.
 */
// TODO [Spring 5]: Check if the @Value annotations are still needed after migrating
public interface IAuthorAlbum {
	/**
	 * Gets the Author ID.
	 * 
	 * @return The ID
	 */
	@Value("#{target.author_id}")
	Long getAuthorId();

	/**
	 * Gets the Album ID for which the Author is an artist.
	 * 
	 * @return The ID
	 */
	@Value("#{target.as_artist}")
	Long getAsArtist();

	/**
	 * Gets the Album ID for which the Author is a colorist.
	 * 
	 * @return The ID
	 */
	@Value("#{target.as_colorist}")
	Long getAsColorist();

	/**
	 * Gets the Album ID for which the Author is an inker.
	 * 
	 * @return The ID
	 */
	@Value("#{target.as_inker}")
	Long getAsInker();

	/**
	 * Gets the Album ID for which the Author is a translator.
	 * 
	 * @return The ID
	 */
	@Value("#{target.as_translator}")
	Long getAsTranslator();

	/**
	 * Gets the Album ID for which the Author is a writer.
	 * 
	 * @return The ID
	 */
	@Value("#{target.as_writer}")
	Long getAsWriter();
}
