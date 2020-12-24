import { reduce, sortBy, uniqBy } from 'lodash'
import {
	Heading, Bold, Italic, Strike, Underline, BulletList,
	OrderedList, ListItem, Link, Blockquote, HardBreak, HorizontalRule, History, Image
} from 'tiptap-vuetify'

export const tipTapExtensions = [
	History,
	Blockquote,
	Link,
	Underline,
	Strike,
	Italic,
	Image,
	ListItem,
	BulletList,
	OrderedList,
	[Heading, {
		options: {
			levels: [1, 2, 3]
		}
	}],
	Bold,
	HorizontalRule,
	HardBreak
]

/**
 * Extracts arrays of sub-models from a collection of models, merges them,
 * keep unique items (based on the ID) and sort them.
 *
 * @param {*} collection The collection of models
 * @param {String} modelProperty The property to access sub-models in the individual models
 * @param {String|Array<String>} sortProperties The property or properties to sort the models by.
 */
export function mergeModels(collection, modelProperty, sortProperties) {
	const all = reduce(collection, (aggregate, value) => {
		const subModel = value[modelProperty]
		if (Array.isArray(subModel)) {
			// Arrays of models
			aggregate.push(...subModel)
		} else if (subModel && subModel.id) {
			// Plain models
			aggregate.push(subModel)
		}
		return aggregate
	}, [])
	const uniq = uniqBy(all, 'id')
	return sortBy(uniq, [sortProperties])
}
