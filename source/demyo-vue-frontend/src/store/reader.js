export default {
	namespaced: true,

	state: {
		currentReader: {}
	},

	mutations: {
		setCurrentReader(state, reader) {
			state.currentReader = reader
		}
	},

	actions: {
		setCurrentReader({ commit }, reader) {
			commit('setCurrentReader', reader)
		}
	}
}
