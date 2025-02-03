import type { RouteLocationNormalizedLoadedGeneric } from "vue-router"

/**
 * Gets the standard ID parameter from the current route, parsed to a number.
 * @param route The current route
 * @returns The parsed ID
 */
export function getParsedId(route: RouteLocationNormalizedLoadedGeneric): number {
	let id
	if (route.params.id instanceof Array) {
		id = route.params.id[0]
	} else {
		id = route.params.id
	}
	return parseInt(id, 10)
}
