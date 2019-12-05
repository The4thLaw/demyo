import axios from 'axios'
import { apiRoot } from '@/myenv'

async function axiosGet(url, defaultValue) {
	let response
	try {
		response = await axios.get(url)
	} catch (e) {
		console.warn(`Failed to get the resource at ${url}`, e)
		return defaultValue
	}
	return response.data
}

export default {
	async findForIndex() {
		let data = await axiosGet(`${apiRoot}authors/`, [])
		return data
	},

	/**
	 * Finds an Author by its ID.
	 * @param {Number} id The Author ID
	 * @return The Author
	 */
	async findById(id) {
		let data = await axiosGet(`${apiRoot}authors/${id}`, {})
		return data
	},

	/**
	 * Finds the Albums on which an Author has worked.
	 * @param {Number} id The Author ID
	 */
	async getAuthorAlbums(id) {
		let data = await axiosGet(`${apiRoot}authors/${id}/albums`, {})
		return data
	},

	/**
	 * Deletes an Author.
	 * @param {Number} id The Author ID
	 * @return {Promise} A Promise that resolves when the author is deleted successfully.
	 */
	deleteAuthor(id) {
		return axios
			.delete(`${apiRoot}authors/${id}`)
			.catch(e => console.log(`Failed to delete Author ${id}`, e))
	}
}
