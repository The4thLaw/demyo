export default {
	namespaced: true,

	state: {
		currentReader: {},
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
		}
	},

	actions: {
		setCurrentReader({ commit }, reader) {
			commit('setCurrentReader', reader)
			commit('setRequireReaderSelection', false)
		},

		requireReaderSelection({ commit }) {
			commit('setRequireReaderSelection', true)
		}
	}
}
