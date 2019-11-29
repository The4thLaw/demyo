import axios from 'axios'
import { apiRoot } from '@/myenv'

export default {
	async findForIndex() {
		let response = await axios.get(`${apiRoot}authors/`)
		return response.data
	},

	/**
	 * Finds an Author by its ID.
	 * @param {Number} id The Author ID
	 * @return The Author
	 */
	async findById(id) {
		let response = await axios.get(`${apiRoot}authors/${id}`)
		return response.data
	},

	/**
	 * Finds the Albums on which an Author has worked.
	 * @param {Number} id The Author ID
	 */
	async getAuthorAlbums(id) {
		let response = await axios.get(`${apiRoot}authors/${id}/albums`)
		return response.data
	}
}
