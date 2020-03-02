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

		setFavouriteAlbums(state, list) {
			list = list || []
			state.favouriteAlbums = list.sort((a, b) => a - b)
		},

		setReadingList(state, list) {
			list = list || []
			state.readingList = list.sort((a, b) => a - b)
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
		}
	}
}
