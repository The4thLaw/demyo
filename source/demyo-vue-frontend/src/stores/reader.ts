import sortedIndex from 'lodash/sortedIndex'
import sortedIndexOf from 'lodash/sortedIndexOf'
import { defineStore } from 'pinia'

interface ReaderLists {
	/** The IDs of the reader's favourite series. */
	favouriteSeries: number[]
	/** The IDs of the reader's favourite albums. */
	favouriteAlbums: number[]
	/** The IDs of the albums in the reader's reading list */
	readingList: number[]
}

/**
 * Inserts an item at the right position in a sorted array.
 *
 * @param list The array to modify
 * @param item The item to insert
 */
function insert(list: number[], item: number) {
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
 * @param list The array to modify
 * @param item The item to remove
 */
function remove(list: number[], item: number) {
	const index = sortedIndexOf(list, item)
	if (index >= 0) {
		list.splice(index, 1)
	}
}

/**
 * Pre-processes a list for assignment
 * @param list The list to modify
 * @return The modified list
 */
function preprocess(list: number[]): number[] {
	list = list || []
	return list.sort((a, b) => a - b)
}

export const useReaderStore = defineStore('reader', {

	state: () => ({
		currentReader: {},
		// Sets would be nice, but Vue won't support reactive sets until sometime in v3
		favouriteSeries: [] as number[],
		favouriteAlbums: [] as number[],
		readingList: [] as number[],
		readerLoaded: false,
		readerSelectionRequired: false
	}),

	actions: {
		// TODO: TypeScript: define a type for the reader
		setCurrentReader(reader: any) {
			this.currentReader = reader
			this.readerSelectionRequired = false
			this.readerLoaded = true
		},

		requireReaderSelection() {
			this.readerSelectionRequired = true
			this.readerLoaded = false
		},

		setReaderLists(lists: ReaderLists) {
			this.favouriteSeries = preprocess(lists.favouriteSeries)
			this.favouriteAlbums = preprocess(lists.favouriteAlbums)
			this.readingList = preprocess(lists.readingList)
		},

		setReadingList(list: number[]) {
			this.readingList = preprocess(list)
		},

		addFavouriteSeries(item: number) {
			insert(this.favouriteSeries, item)
		},

		removeFavouriteSeries(item: number) {
			remove(this.favouriteSeries, item)
		},

		addFavouriteAlbum(item: number) {
			insert(this.favouriteAlbums, item)
		},

		removeFavouriteAlbum(item: number) {
			remove(this.favouriteAlbums, item)
		},

		addToReadingList(item: number) {
			insert(this.readingList, item)
		},

		removeFromReadingList(item: number) {
			remove(this.readingList, item)
		}
	}
})
