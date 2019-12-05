/**
 * Requests confirmation from the user before performing an action.
 *
 * @param {Function} handler The handler to call upon confirmation
 * @param {String} message The confirmation message to show to the user
 */
export function confirmAsyncAction(handler, message) {
	if (confirm(message)) {
		// TODO: use a vuetify dialog
		return handler()
	}
	console.log('User rejected the action')
	return new Promise()
}
