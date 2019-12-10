import { axiosGet, axiosDelete } from '@/helpers/axios'

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
	 * Deletes a Model.
	 * @param {Number} id The Model ID
	 */
	async deleteModel(id) {
		let data = await axiosDelete(this.basePath + id, false)
		return data
	}
}

export default AbstractModelService
