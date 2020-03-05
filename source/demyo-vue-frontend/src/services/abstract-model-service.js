import { isInteger } from 'lodash'
import { axiosGet, axiosPost, axiosPut, axiosDelete } from '@/helpers/axios'

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
		return axiosGet(this.basePath + '?view=minimal', [])
	}

	/**
	 * Finds a Model by its ID.
	 * @param {Number} id The Model ID
	 * @return {Promise<any>} The Model
	 */
	async findById(id) {
		let model = await axiosGet(this.basePath + id, {})

		if (this.config.fillMissingObjects) {
			this.config.fillMissingObjects.forEach(prop => {
				if (!model[prop]) {
					model[prop] = {}
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
		let idString = ids.join(',')
		return axiosGet(`${this.basePath}/batch/${idString}`, {})
	}

	save(model) {
		if (this.config.sanitizeArrays) {
			// Transform arrays of integers in arrays of objects
			this.config.sanitizeArrays.forEach(prop => {
				if (Array.isArray(model[prop])) {
					model[prop] = model[prop].map(v => isInteger(v) ? { id: v } : v)
				}
			})
		}

		if (this.config.sanitizeObjects) {
			// In the case of clearable model link fields, the id can be set to false
			// In such cases, we should clear the object completely
			this.config.sanitizeObjects.forEach(prop => {
				if (!model[prop].id) {
					delete model[prop]
				}
			})
		}

		if (model.id) {
			return axiosPut(this.basePath + model.id, model, -1)
		}
		return axiosPost(this.basePath, model, -1)
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
