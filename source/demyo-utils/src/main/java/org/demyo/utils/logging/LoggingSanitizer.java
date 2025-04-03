package org.demyo.utils.logging;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * This class provides methods to sanitize user-provided parameters in logging calls.
 */
public final class LoggingSanitizer {
	private static final Pattern NEWLINES = Pattern.compile("[\n\r]");

	private LoggingSanitizer() {
		// Utility class
	}

	/**
	 * Sanitizes a user-provided value to include it as a logging parameter.
	 * @param input The value.
	 * @return The sanitized copy.
	 */
	public static String sanitize(String input) {
		return NEWLINES.matcher(input).replaceAll("_NEWLINE_");
	}

	/**
	 * Sanitizes user-provided values to include them as a logging parameter.
	 * @param input The values.
	 * @return The sanitized copy.
	 */
	public static List<String> sanitize(String... input) {
		return Stream.of(input).map(LoggingSanitizer::sanitize).toList();
	}

	/**
	 * Sanitizes user-provided values to include them as a logging parameter.
	 * @param input The values.
	 * @return The sanitized copy.
	 */
	public static List<String> sanitize(List<String> input) {
		return input.stream().map(LoggingSanitizer::sanitize).toList();
	}
}
