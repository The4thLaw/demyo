package org.demyo.model;

import java.util.TreeSet;
import java.util.function.Supplier;

import org.springframework.test.util.ReflectionTestUtils;

import org.demyo.model.util.AlbumComparator;
import org.demyo.model.util.IdentifyingNameComparator;

/**
 * Models builds for unit tests.
 */
public final class TestModelBuilders {
	private static <M extends IModel> M createModel(Supplier<M> supplier, long id) {
		M model = supplier.get();
		model.setId(id);
		return model;
	}

	/**
	 * Creates an Album with the provided data.
	 *
	 * @param id The desired ID
	 * @param title The desired title
	 * @return The created album
	 */
	public static Album albumByTitle(long id, String title) {
		Album a = createModel(Album::new, id);
		a.setTitle(title);
		ReflectionTestUtils.setField(a, "artists", new TreeSet<Author>(new IdentifyingNameComparator()));
		ReflectionTestUtils.setField(a, "taxons", new TreeSet<Taxon>(new IdentifyingNameComparator()));
		ReflectionTestUtils.setField(a, "writers", new TreeSet<Author>(new IdentifyingNameComparator()));
		return a;
	}

	/**
	 * Creates an Author with the provided data.
	 *
	 * @param id The desired ID
	 * @param fname The desired first name
	 * @param name The desired last name
	 * @return The created author
	 */
	public static Author authorByName(long id, String fname, String name) {
		Author a = createModel(Author::new, id);
		a.setFirstName(fname);
		a.setName(name);
		return a;
	}

	/**
	 * Creates a Series with the provided data.
	 *
	 * @param id The desired ID
	 * @param name The desired name
	 * @return The created series
	 */
	public static Series seriesByName(long id, String name) {
		Series s = createModel(Series::new, id);
		s.setName(name);
		ReflectionTestUtils.setField(s, "albums", new TreeSet<Album>(new AlbumComparator()));
		return s;
	}

	/**
	 * Creates a Taxon with the provided data.
	 *
	 * @param id The desired ID
	 * @param name The desired name
	 * @return The created taxon
	 */
	public static Taxon taxonByName(long id, String name) {
		Taxon t = createModel(Taxon::new, id);
		t.setName(name);
		return t;
	}

	private TestModelBuilders() {
		// Utility class
	}
}
