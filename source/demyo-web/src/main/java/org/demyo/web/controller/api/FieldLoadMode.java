package org.demyo.web.controller.api;

import java.util.Locale;

/**
 * Defines the way model fields are loaded in the API.
 */
public enum FieldLoadMode {
	/** Automatic field loading. May involve post-processing. */
	AUTO,
	/** Raw field loading. Non-essential post-processing is bypassed. */
	RAW;

	/**
	 * Similar to {@link #valueOf(String)}, but ignoring the case of the input value.
	 * @param value The string representation
	 */
	public static FieldLoadMode valueOfIgnoreCase(String value) {
		value = value.toUpperCase(Locale.ROOT);
		return valueOf(value);
	}
}
