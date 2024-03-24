import sortBy from 'lodash/sortBy'
import uniqBy from 'lodash/uniqBy'

// TODO: Vue 3: rich text
/*
import {
	Blockquote,
	Bold,
	BulletList,
	HardBreak,
	Heading,
	History,
	HorizontalRule,
	Image,
	Italic,
	Link,
	ListItem,
	OrderedList,
	Strike, Underline
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
*/
export const tipTapExtensions = []

/**
 * Extracts arrays of sub-models from a collection of models, merges them,
 * keep unique items (based on the ID) and sort them.
 *
 * @param {*} collection The collection of models
 * @param {String} modelProperty The property to access sub-models in the individual models
 * @param {String|Array<String>} sortProperties The property or properties to sort the models by.
 */
export function mergeModels(collection, modelProperty, sortProperties) {
	if (!Array.isArray(sortProperties)) {
		sortProperties = [sortProperties]
	}
	const all = Object.values(collection).reduce((aggregate, value) => {
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
	return sortBy(uniq, sortProperties)
}
