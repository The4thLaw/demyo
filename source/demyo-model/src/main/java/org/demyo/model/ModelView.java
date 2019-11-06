package org.demyo.model;

import java.util.Optional;

/**
 * Jackson views common to all {@link IModel}s.
 */
public final class ModelView {
	/**
	 * Private constructor for utility class.
	 */
	private ModelView() {

	}

	/**
	 * Returns a view based on its case-insensitive name.
	 * <p>
	 * If the name is null or invalid, the Basic view is returned.
	 * </p>
	 * 
	 * @param name The name of the view.
	 * @return The view class.
	 */
	public static final Class<?> byName(String name) {
		return byName(Optional.ofNullable(name));
	}

	/**
	 * Returns a view based on its case-insensitive name.
	 * <p>
	 * If the name is missing or invalid, the Basic view is returned.
	 * </p>
	 * 
	 * @param name The name of the view.
	 * @return The view class.
	 */
	public static final Class<?> byName(Optional<String> name) {
		if (!name.isPresent()) {
			return Basic.class;
		}

		String normalizedName = name.get().toLowerCase();

		switch (normalizedName) {
			case "minimal":
				return Minimal.class;
			case "basic":
			default:
				return Basic.class;
		}
	}

	/**
	 * The minimal view return the technical ID and identifying name.
	 */
	public interface Minimal {
	}

	/**
	 * The basic view returns some information that can be used to display short summaries of the models.
	 *
	 */
	public interface Basic extends Minimal {
	}
}
