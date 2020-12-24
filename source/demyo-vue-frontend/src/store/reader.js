import { sortedIndex, sortedIndexOf } from 'lodash'

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

export default {
	namespaced: true,

	state: {
		currentReader: {},
		// Sets would be nice, but Vue won't support reactive sets until sometime in v3
		favouriteSeries: [],
		favouriteAlbums: [],
		readingList: [],
		readerLoaded: false,
		requireReaderSelection: false
	},

	mutations: {
		setCurrentReader(state, reader) {
			state.currentReader = reader
		},

		setRequireReaderSelection(state, val) {
			state.requireReaderSelection = val
			state.readerLoaded = !val
		},

		setFavouriteSeries(state, list) {
			list = list || []
			state.favouriteSeries = list.sort((a, b) => a - b)
		},

		addFavouriteSeries(state, item) {
			insert(state.favouriteSeries, item)
		},

		removeFavouriteSeries(state, item) {
			remove(state.favouriteSeries, item)
		},

		setFavouriteAlbums(state, list) {
			list = list || []
			state.favouriteAlbums = list.sort((a, b) => a - b)
		},

		addFavouriteAlbum(state, item) {
			insert(state.favouriteAlbums, item)
		},

		removeFavouriteAlbum(state, item) {
			remove(state.favouriteAlbums, item)
		},

		setReadingList(state, list) {
			list = list || []
			state.readingList = list.sort((a, b) => a - b)
		},

		addToReadingList(state, item) {
			insert(state.readingList, item)
		},

		removeFromReadingList(state, item) {
			remove(state.readingList, item)
		}
	},

	actions: {
		setCurrentReader({ commit }, reader) {
			commit('setCurrentReader', reader)
			commit('setRequireReaderSelection', false)
		},

		requireReaderSelection({ commit }) {
			commit('setRequireReaderSelection', true)
		},

		setReaderLists({ commit }, lists) {
			commit('setFavouriteSeries', lists.favouriteSeries)
			commit('setFavouriteAlbums', lists.favouriteAlbums)
			commit('setReadingList', lists.readingList)
		},

		setReadingList({ commit }, list) {
			commit('setReadingList', list)
		},

		addFavouriteSeries({ commit }, item) {
			commit('addFavouriteSeries', item)
		},

		removeFavouriteSeries({ commit }, item) {
			commit('removeFavouriteSeries', item)
		},

		addFavouriteAlbum({ commit }, item) {
			commit('addFavouriteAlbum', item)
		},

		removeFavouriteAlbum({ commit }, item) {
			commit('removeFavouriteAlbum', item)
		},

		addToReadingList({ commit }, item) {
			commit('addToReadingList', item)
		},

		removeFromReadingList({ commit }, item) {
			commit('removeFromReadingList', item)
		}
	}
}
