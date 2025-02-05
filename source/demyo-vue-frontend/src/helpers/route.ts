import type { LocationQueryValue, RouteLocationNormalizedLoadedGeneric } from "vue-router"

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

/**
 * Parsed a route parameter to a number.
 * @param param The route parameter to parse.
 * @returns The parsed ID.
 */
export function getParsedRouteParam(param: string | LocationQueryValue[]): number | null {
	let id
	if (param instanceof Array) {
		id = param[0]
		if (id === null) {
			return null
		}
	} else {
		id = param
	}
	return parseInt(id, 10)
}
