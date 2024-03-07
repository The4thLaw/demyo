import { delay } from 'lodash'
import { defineStore } from 'pinia'

export const useUiStore = defineStore('ui', {
	state: () => ({
		suppressSearch: false,
		globalOverlay: false,
		displaySnackbar: false,
		snackbarMessages: [],
		displayDetailsPane: false
	}),

	actions: {
		enableSearch() {
			this.suppressSearch = false
		},

		disableSearch() {
			this.suppressSearch = true
		},

		enableGlobalOverlay() {
			this.globalOverlay = true
		},

		disableGlobalOverlay() {
			this.globalOverlay = false
		},

		showSnackbar(message) {
			this.snackbarMessages.push(message)
			if (!this.displaySnackbar) {
				this.displaySnackbar = true
			}
		},

		closeSnackbar() {
			this.displaySnackbar = false
			this.snackbarMessages.shift()
			delay(() => this.nextSnackbarMessage(), 1000)
		},

		nextSnackbarMessage() {
			if (this.snackbarMessages[0] && !this.displaySnackbar) {
				this.displaySnackbar = true
			}
		},

		showDetailsPane() {
			this.displayDetailsPane = true
		},

		hideDetailsPane() {
			this.displayDetailsPane = false
		}
	}
})
