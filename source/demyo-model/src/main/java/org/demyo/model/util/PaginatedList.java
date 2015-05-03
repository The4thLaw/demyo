package org.demyo.model.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PaginatedList<T> implements List<T> {

	/** The list of items for the current page. */
	private final List<T> pageList;
	/** The current page number. */
	private final int currentPage;
	/** The number of items in one page. */
	private final int pageSize;
	/** The total amount of items in the non-paginated list. */
	private final long total;

	/**
	 * Creates a paginated list.
	 * 
	 * @param pageList The list of items for the current page.
	 * @param currentPage The current page number.
	 * @param pageSize The number of items in one page.
	 * @param total The total amount of items in the non-paginated list.
	 */
	public PaginatedList(List<T> pageList, int currentPage, int pageSize, long total) {
		if (pageList == null) {
			throw new IllegalArgumentException("null pageList");
		}
		if (pageSize < pageList.size()) {
			throw new IllegalArgumentException("There are more items in pageList than the expected pageSize");
		}
		this.pageList = Collections.unmodifiableList(pageList);
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.total = total;
	}

	/**
	 * Gets the list of items for the current page.
	 * 
	 * @return the list of items for the current page
	 */
	public List<T> getPageList() {
		return pageList;
	}

	/**
	 * Gets the current page number.
	 * 
	 * @return the current page number
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * Gets the number of items in one page.
	 * 
	 * @return the number of items in one page
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Gets the total amount of items in the non-paginated list.
	 * 
	 * @return the total amount of items in the non-paginated list
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * Gets the number of pages, based on the total number of items and number of items per page.
	 * 
	 * @return The number of pages.
	 */
	public int getMaxPages() {
		return (int) Math.ceil((double) total / pageSize);
	}

	/**
	 * Checks if this page has a previous page.
	 * 
	 * @return <code>true</code> if this page is not the first one.
	 */
	public boolean hasPreviousPage() {
		return currentPage > 1;
	}

	/**
	 * Checks if this page has a next page.
	 * 
	 * @return <code>true</code> if this page is not the last one.
	 */
	public boolean hasNextPage() {
		return currentPage < getMaxPages();
	}

	/* Only delegate methods below this point. */

	@Override
	public int size() {
		return pageList.size();
	}

	@Override
	public boolean isEmpty() {
		return pageList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return pageList.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return pageList.iterator();
	}

	@Override
	public Object[] toArray() {
		return pageList.toArray();
	}

	@Override
	public <V> V[] toArray(V[] a) {
		return pageList.toArray(a);
	}

	@Override
	public boolean add(T e) {
		return pageList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return pageList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return pageList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return pageList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return pageList.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return pageList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return pageList.retainAll(c);
	}

	@Override
	public void clear() {
		pageList.clear();
	}

	@Override
	public boolean equals(Object o) {
		return pageList.equals(o);
	}

	@Override
	public int hashCode() {
		return pageList.hashCode();
	}

	@Override
	public T get(int index) {
		return pageList.get(index);
	}

	@Override
	public T set(int index, T element) {
		return pageList.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		pageList.add(index, element);
	}

	@Override
	public T remove(int index) {
		return pageList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return pageList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return pageList.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return pageList.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return pageList.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return pageList.subList(fromIndex, toIndex);
	}
}
