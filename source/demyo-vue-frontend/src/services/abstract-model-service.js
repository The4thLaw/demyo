import { axiosDelete, axiosGet, axiosPost, axiosPut } from '@/helpers/axios'

/**
 * Base class for Model API services.
 */
class AbstractModelService {
	/**
	 * Constructor
	 * @param {String} basePath The base path for API URLs, relative to the API root
	 * @param {*} config An optional configuration object
	 */
	constructor(basePath, config) {
		this.basePath = basePath
		this.config = config || {}
	}

	findForIndex() {
		return axiosGet(this.basePath, [])
	}

	findForList() {
		return axiosGet(this.basePath, { view: 'minimal' }, [])
	}

	/**
	 * Finds a Model by its ID.
	 * @param {Number} id The Model ID
	 * @return {Promise<any>} The Model
	 */
	async findById(id) {
		const model = await axiosGet(this.basePath + id, {})
		return this.fillMissingData(model)
	}

	/**
	 * Fills any data that is required by the frontend but missing from server-side data.
	 * @param {*} model The model to fill
	 */
	fillMissingData(model) {
		if (model instanceof Promise) {
			console.error('You need to resolve the Promises before calling fillMissingData')
		}

		if (this.config.fillMissingObjects) {
			this.config.fillMissingObjects.forEach(prop => {
				if (!model[prop]) {
					model[prop] = {
						id: undefined
					}
				}
			})
		}

		if (this.config.fillMissingArrays) {
			this.config.fillMissingArrays.forEach(prop => {
				if (!model[prop]) {
					model[prop] = []
				}
			})
		}

		return model
	}

	/**
	 * Finds a set of Models by their IDs.
	 * @param {Number[]} ids The Model IDs
	 * @return {Promise<any[]>} The Models
	 */
	findMultipleById(ids) {
		const idString = ids.join(',')
		return axiosGet(`${this.basePath}/batch/${idString}`, {})
	}

	save(model) {
		this.sanitizeArrays(model)
		this.sanitizeObjects(model)
		this.sanitizeHtml(model)

		if (model.id) {
			return axiosPut(this.basePath + model.id, model, -1)
		}
		return axiosPost(this.basePath, model, -1)
	}

	/**
	 * Sanitizes the sub-properties of a Model that link to multiple other Models.
	 * Transforms arrays of integers in arrays of objects.
	 * @param {*} model The Model.
	 * @private
	 */
	 sanitizeArrays(model) {
		if (this.config.sanitizeArrays) {
			this.config.sanitizeArrays.forEach(prop => {
				if (Array.isArray(model[prop])) {
					model[prop] = model[prop].map(v => Number.isInteger(v) ? { id: v } : v)
				}
			})
		}
	}

	/**
	 * Sanitizes the sub-properties of a Model that link to a single other Model.
	 * @param {*} model The Model.
	 * @private
	 */
	sanitizeObjects(model) {
		if (this.config.sanitizeObjects) {
			// In the case of clearable model link fields, the id can be set to false
			// In such cases, we should clear the object completely
			this.config.sanitizeObjects.forEach(prop => {
				if (!model[prop].id) {
					delete model[prop]
				}
			})
		}
	}

	/**
	 * Sanitizes the HTML properties of a Model, clearing empty paragraphs left behind by the RTE.
	 * @param {*} model The Model.
	 * @private
	 */
	sanitizeHtml(model) {
		if (this.config.sanitizeHtml) {
			// Rich text could leave empty <p></p> markup if the user deletes the text
			this.config.sanitizeHtml.forEach(prop => {
				if (model[prop] === '<p></p>') {
					model[prop] = null
				}
			})
		}
	}

	/**
	 * Deletes a Model.
	 * @param {Number} id The Model ID
	 */
	deleteModel(id) {
		return axiosDelete(this.basePath + id, false)
	}
}

export default AbstractModelService
