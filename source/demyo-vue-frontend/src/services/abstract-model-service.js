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

	async findForIndex() {
		let data = await axiosGet(this.basePath, [])
		return data
	}

	async findForList() {
		let data = await axiosGet(this.basePath + '?view=minimal', [])
		return data
	}

	/**
	 * Finds a Model by its ID.
	 * @param {Number} id The Model ID
	 * @return The Model
	 */
	async findById(id) {
		let data = await axiosGet(this.basePath + id, {})
		return data
	}

	/**
	 * Finds a set of Models by their IDs.
	 * @param {Number[]} ids The Model IDs
	 * @return {Object[]} The Models
	 */
	async findMultipleById(ids) {
		let idString = ids.join(',')
		let data = await axiosGet(`${this.basePath}/batch/${idString}`, {})
		return data
	}

	async save(model) {
		let data
		if (model.id) {
			data = await axiosPut(this.basePath + model.id, model, -1)
		} else {
			data = await axiosPost(this.basePath, model, -1)
		}
		return data
	}

	/**
	 * Deletes a Model.
	 * @param {Number} id The Model ID
	 */
	async deleteModel(id) {
		let data = await axiosDelete(this.basePath + id, false)
		return data
	}
}

export default AbstractModelService
