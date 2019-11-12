export default {
	namespaced: true,

	state: {
		suppressSearch: false
	},

	mutations: {
		toggleSearch(state, enabled) {
			state.suppressSearch = !enabled
		}
	},

	actions: {
		enableSearch({ commit }) {
			commit('toggleSearch', true)
		},

		disableSearch({ commit }) {
			commit('toggleSearch', false)
		}
	}
}
