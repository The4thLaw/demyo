import i18n from '@/i18n'
import router from '@/router'
import { useUiStore } from '@/stores/ui'

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
		uiStore.showSnackbar(i18n.global.t(confirmationLabel))
		router.push({ name: routeName })
	}
}
