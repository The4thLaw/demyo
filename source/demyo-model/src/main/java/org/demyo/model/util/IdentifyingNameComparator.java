package org.demyo.model.util;

import java.util.Comparator;

import org.demyo.model.IModel;

/**
 * A {@link Comparator} that sorts elements according to their {@link IModel#getIdentifyingName()} property.
 */
public class IdentifyingNameComparator extends AbstractModelComparator<IModel> {
	private static final long serialVersionUID = 3093979330573041997L;

	@Override
	public int compare(IModel m1, IModel m2) {
		int comparison = nullSafeCollatingComparison(m1.getIdentifyingName(), m2.getIdentifyingName());
		if (comparison != 0) {
			return comparison;
		}
		return defaultComparison(m1, m2);
	}
}
