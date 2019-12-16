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
	component.$store.dispatch('ui/enableGlobalOverlay')
	let id = await handler()
	component.$store.dispatch('ui/disableGlobalOverlay')
	if (id <= 0) {
		component.$store.dispatch('ui/showSnackbar', component.$t('core.exception.api.title'))
	} else {
		component.$router.push({ name: routeName, params: { id: id } })
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
		component.$store.dispatch('ui/showSnackbar', component.$t(confirmationLabel))
		component.$router.push({ name: routeName })
	}
}
