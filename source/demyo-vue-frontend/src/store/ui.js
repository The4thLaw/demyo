import { delay } from 'lodash'

export default {
	namespaced: true,

	state: {
		suppressSearch: false,
		globalOverlay: false,
		displaySnackbar: false,
		snackbarMessages: []
	},

	mutations: {
		toggleSearch(state, enabled) {
			state.suppressSearch = !enabled
		},

		toggleGlobalOverlay(state, enabled) {
			state.globalOverlay = enabled
		},

		showSnackbar(state, message) {
			state.snackbarMessages.push(message)
			if (!state.displaySnackbar) {
				state.displaySnackbar = true
			}
		},

		closeSnackbar(state) {
			state.displaySnackbar = false
			state.snackbarMessages.shift()
		},

		nextSnackbarMessage(state) {
			if (state.snackbarMessages[0] && !state.displaySnackbar) {
				state.displaySnackbar = true
			}
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
		},

		showSnackbar({ commit }, message) {
			commit('showSnackbar', message)
		},

		closeSnackbar({ commit }) {
			commit('closeSnackbar')
			delay(() => commit('nextSnackbarMessage'), 1000)
		}
	}
}
