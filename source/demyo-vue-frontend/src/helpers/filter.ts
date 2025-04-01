import type { RouteLocationNormalizedGeneric } from 'vue-router'

/**
 * Retrieves a filter from the route parameters.
 * @param {Route} route The Vue route
 * @return a filter object, suitable for the API calls.
 */
export function retrieveFilter<T extends AbstractModelFilter>(route: RouteLocationNormalizedGeneric): T | undefined {
	const filter = {} as T
	for (const key of Object.keys(route.query)) {
		if (key.startsWith('with')) {
			const newKey = key.replace(/^with/, '').toLowerCase()
			filter[newKey as keyof T] = route.query[key] as any
		}
	}

	if (!Object.keys(filter).length) {
		return undefined
	}

	// Default to AND
	// Bit weird, the enum isn't found so we have to cast
	filter.mode = route.query.mode === 'OR' ? 'OR' : 'AND'

	return filter
}
