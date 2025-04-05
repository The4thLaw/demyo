import type { RouteLocationNormalizedGeneric } from 'vue-router'
import { getParsedRouteParam } from './route'

/**
 * Retrieves a filter from the route parameters.
 * @param {Route} route The Vue route
 * @return a filter object, suitable for the API calls.
 */
// T is used in the method body...
// eslint-disable-next-line @typescript-eslint/no-unnecessary-type-parameters
export function retrieveFilter<T extends AbstractModelFilter>(route: RouteLocationNormalizedGeneric): T | undefined {
	const filter = {} as T
	for (const key of Object.keys(route.query)) {
		if (key.startsWith('with')) {
			const newKey = key.replace(/^with/, '').toLowerCase()
			const val = getParsedRouteParam(route.query[key])
			if (val) {
				// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-explicit-any
				filter[newKey as keyof T] = val as any
			}
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
