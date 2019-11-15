export default {
	namespaced: true,

	state: {
		suppressSearch: false,
		globalOverlay: false
	},

	mutations: {
		toggleSearch(state, enabled) {
			state.suppressSearch = !enabled
		},

		toggleGlobalOverlay(state, enabled) {
			state.globalOverlay = enabled
		}
	},

	actions: {
		enableSearch({ commit }) {
			commit('toggleSearch', true)
		},

		disableSearch({ commit }) {
			commit('toggleSearch', false)
		},

		enableGlobalOverlay({ commit }) {
			commit('toggleGlobalOverlay', true)
		},

		disableGlobalOverlay({ commit }) {
			commit('toggleGlobalOverlay', false)
		}
	}
}
