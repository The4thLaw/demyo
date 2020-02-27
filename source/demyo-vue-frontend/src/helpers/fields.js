import { reduce, sortBy, uniqBy } from 'lodash'
import { Heading, Bold, Italic, Strike, Underline, BulletList,
	OrderedList, ListItem, Link, Blockquote, HardBreak, HorizontalRule, History, Image } from 'tiptap-vuetify'

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
	let all = reduce(collection, (aggregate, value) => {
		let subModel = value[modelProperty]
		if (Array.isArray(subModel)) {
			aggregate.push(...subModel)
		}
		return aggregate
	}, [])
	let uniq = uniqBy(all, 'id')
	return sortBy(uniq, [sortProperties])
}
