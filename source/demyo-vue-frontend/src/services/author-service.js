import axios from 'axios'
import { apiRoot } from '@/myenv'

export default {
	async findForIndex() {
		let response = await axios.get(`${apiRoot}authors/`)
		return response.data
	},

	/**
	 * Finds an Author by its ID.
	 * @param {Number} id The technical identifier of the Author
	 * @return The Author
	 */
	async findById(id) {
		let response = await axios.get(`${apiRoot}authors/${id}`)
		return response.data
	}
}
