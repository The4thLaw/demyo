import i18n from '@/i18n'
import router from '@/router'
import store from '@/store'

/**
 * Stub for a save action in an edit component.
 * @param {Vue} component The Vue component
 * @param {Function} handler The function that will perform the save
 * @param {String} routeName The named route to redirect to on success
 */
export async function saveStub(component, handler, routeName) {
	if (!component.$refs.form.validate()) {
		return
	}
	store.dispatch('ui/enableGlobalOverlay')
	let id = await handler()
	store.dispatch('ui/disableGlobalOverlay')
	if (id <= 0) {
		store.dispatch('ui/showSnackbar', i18n.t('core.exception.api.title'))
	} else {
		router.push({ name: routeName, params: { id: id } })
	}
}

/**
 * Stub for a delete action in a view component.
 * @param {Vue} component The Vue component
 * @param {Function} handler The function that will perform the deletion
 * @param {String} confirmationLabel The confirmation snackbar label key
 * @param {String} routeName The named route to redirect to on success
 */
export async function deleteStub(component, handler, confirmationLabel, routeName) {
	component.appTasksMenu = false
	let deleted = await handler()
	if (deleted) {
		store.dispatch('ui/showSnackbar', i18n.t(confirmationLabel))
		router.push({ name: routeName })
	}
}
