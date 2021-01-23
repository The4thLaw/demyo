/**
 * Retrieves a filter from the route parameters.
 * @param {Route} route The Vue route
 * @return a filter object, suitable for the API calls.
 */
export function retrieveFilter(route) {
	const filter = {}
	for (const key of Object.keys(route.query)) {
		if (key.startsWith('with')) {
			const newKey = key.replace(/^with/, '').toLowerCase()
			filter[newKey] = route.query[key]
		}
	}

	if (!Object.keys(filter).length) {
		return null
	}

	// Default to AND
	filter.mode = route.query.mode === 'OR' ? 'OR' : 'AND'

	return filter
}
