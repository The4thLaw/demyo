package org.demyo.dao;

import java.util.Set;

/**
 * Custom methods to supplement the default offering of Spring Data.
 */
public interface IImageCustomRepo {
	/**
	 * Finds the path to all known images.
	 * 
	 * @return The set of known paths.
	 */
	Set<String> findAllPaths();
}
