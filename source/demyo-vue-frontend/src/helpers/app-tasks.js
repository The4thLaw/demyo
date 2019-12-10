/**
 * Stub for a delete action in a view component.
 * @param {Vue} component The Vue component
 * @param {Function} handler The function that will perform the deletion
 * @param {String} confirmationLabel The confirmation snackbar label key
 * @param {String} path The path to redirect to
 */
export async function deleteStub(component, handler, confirmationLabel, path) {
	component.appTasksMenu = false
	let deleted = await handler()
	if (deleted) {
		component.$store.dispatch('ui/showSnackbar', component.$t(confirmationLabel))
		component.$router.push({ path: path })
	}
}
