package org.demyo.model.util;

import java.io.Serializable;
import java.util.Comparator;

import org.hibernate.annotations.SortComparator;

/**
 * {@link Comparator} that works on Comparable. Used for {@link SortComparator}.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ComparableComparator implements Comparator<Comparable>, Serializable {
	private static final long serialVersionUID = 4093459448029858856L;

	@Override
	public int compare(Comparable a1, Comparable a2) {
		return a1.compareTo(a2);
	}
}
