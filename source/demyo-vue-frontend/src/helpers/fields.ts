import sortBy from 'lodash/sortBy'
import uniqBy from 'lodash/uniqBy'

/**
 * Extracts arrays of sub-models from a collection of models, merges them,
 * keep unique items (based on the ID) and sort them.
 *
 * @param collection The collection of models
 * @param modelProperty The property to access sub-models in the individual models
 * @param sortProperties The property or properties to sort the models by.
 */
export function mergeModels<T extends IModel, S extends IModel>(collection: T[], modelProperty: keyof T,
	sortProperties: (keyof S)[]|(keyof S)): S[] {
	if (!Array.isArray(sortProperties)) {
		sortProperties = [sortProperties]
	}
	const all = Object.values(collection).reduce((aggregate: S[], value: T) => {
		const subModel = value[modelProperty] as S | undefined
		if (Array.isArray(subModel)) {
			// Arrays of models
			aggregate.push(...subModel)
		} else if (subModel?.id) {
			// Plain models
			aggregate.push(subModel)
		}
		return aggregate
	}, [])
	const uniq = uniqBy(all, 'id')
	return sortBy(uniq, sortProperties)
}
