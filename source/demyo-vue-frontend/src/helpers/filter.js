import { reduce } from 'lodash'


/**
 * Retrieves a filter from the route parameters.
 * @param {Route} route The Vue route
 * @return a filter object, suitable for the API calls.
 */
export function retrieveFilter(route) {
	let filter = reduce(route.query, (accumulator, value, /** @type String */ key) => {
		if (key.startsWith('with')) {
			let newKey = key.replace(/^with/, '').toLowerCase()
			accumulator[newKey] = value
		}
		return accumulator
	}, {})

	if (!Object.keys(filter).length) {
		return null
	}
	return filter
}
