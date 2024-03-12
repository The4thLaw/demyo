import sortedIndex from 'lodash/sortedIndex'
import sortedIndexOf from 'lodash/sortedIndexOf'
import { defineStore } from 'pinia'

/**
 * Inserts an item at the right position in a sorted array.
 *
 * @param {Array<Number>} list The array to modify
 * @param {Number} item The item to insert
 */
function insert(list, item) {
	if (sortedIndexOf(list, item) >= 0) {
		// Don't add twice
		return
	}
	const index = sortedIndex(list, item)
	list.splice(index, 0, item)
}

/**
 * Removes an item from a sorted array.
 *
 * @param {Array<Number>} list The array to modify
 * @param {Number} item The item to remove
 */
function remove(list, item) {
	const index = sortedIndexOf(list, item)
	if (index >= 0) {
		list.splice(index, 1)
	}
}

/**
 * Pre-processes a list for assignment
 * @param {Array<Number>} list The list to modify
 * @return {Array<Number>} The modified list
 */
function preprocess(list) {
	list = list || []
	return list.sort((a, b) => a - b)
}

export const useReaderStore = defineStore('reader', {

	state: () => ({
		currentReader: {},
		// Sets would be nice, but Vue won't support reactive sets until sometime in v3
		favouriteSeries: [],
		favouriteAlbums: [],
		readingList: [],
		readerLoaded: false,
		readerSelectionRequired: false
	}),

	actions: {
		setCurrentReader(reader) {
			this.currentReader = reader
			this.readerSelectionRequired = false
			this.readerLoaded = true
		},

		requireReaderSelection() {
			this.readerSelectionRequired = true
			this.readerLoaded = false
		},

		setReaderLists(lists) {
			this.favouriteSeries = preprocess(lists.favouriteSeries)
			this.favouriteAlbums = preprocess(lists.favouriteAlbums)
			this.readingList = preprocess(lists.readingList)
		},

		setReadingList(list) {
			this.readingList = preprocess(list)
		},

		addFavouriteSeries(item) {
			insert(this.favouriteSeries, item)
		},

		removeFavouriteSeries(item) {
			remove(this.favouriteSeries, item)
		},

		addFavouriteAlbum(item) {
			insert(this.favouriteAlbums, item)
		},

		removeFavouriteAlbum(item) {
			remove(this.favouriteAlbums, item)
		},

		addToReadingList(item) {
			insert(this.readingList, item)
		},

		removeFromReadingList(item) {
			remove(this.readingList, item)
		}
	}
})
