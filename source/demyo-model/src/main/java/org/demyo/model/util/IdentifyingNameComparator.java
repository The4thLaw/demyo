package org.demyo.model.util;

import java.util.Comparator;

import org.demyo.model.IModel;

/**
 * A {@link Comparator} that sorts elements according to their {@link IModel#getIdentifyingName()} property.
 */
public class IdentifyingNameComparator implements Comparator<IModel> {

	@Override
	public int compare(IModel m1, IModel m2) {
		// TODO: make this locale-aware so that accented characters are properly sorted
		return m1.getIdentifyingName().compareTo(m2.getIdentifyingName());
	}

}
