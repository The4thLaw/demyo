import { axiosGet, axiosPost, axiosPut, axiosDelete } from '@/helpers/axios'

/**
 * Base class for Model API services.
 */
class AbstractModelService {
	/**
	 * Constructor
	 * @param {String} basePath The base path for API URLs, relative to the API root
	 */
	constructor(basePath) {
		this.basePath = basePath
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
	findById(id) {
		return axiosGet(this.basePath + id, {})
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
