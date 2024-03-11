import i18n from '@/i18n'
import router from '@/router'
import { useUiStore } from '@/stores/ui'

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
	const uiStore = useUiStore()
	uiStore.enableGlobalOverlay()
	const id = await handler()
	uiStore.disableGlobalOverlay()
	if (id <= 0) {
		uiStore.showSnackbar(i18n.t('core.exception.api.title'))
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
	const deleted = await handler()
	if (deleted) {
		const uiStore = useUiStore()
		uiStore.showSnackbar(i18n.t(confirmationLabel))
		router.push({ name: routeName })
	}
}
