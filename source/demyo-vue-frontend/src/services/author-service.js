import { axiosGet, axiosDelete } from '@/helpers/axios'

// TODO: make an AbstractModelService with prototype-based inheritance

export default {
	async findForIndex() {
		let data = await axiosGet('authors/', [])
		return data
	},

	/**
	 * Finds an Author by its ID.
	 * @param {Number} id The Author ID
	 * @return The Author
	 */
	async findById(id) {
		let data = await axiosGet(`authors/${id}`, {})
		return data
	},

	/**
	 * Finds the Albums on which an Author has worked.
	 * @param {Number} id The Author ID
	 */
	async getAuthorAlbums(id) {
		let data = await axiosGet(`authors/${id}/albums`, {})
		return data
	},

	/**
	 * Deletes an Author.
	 * @param {Number} id The Author ID
	 */
	async deleteAuthor(id) {
		let data = await axiosDelete(`authors/${id}`, false)
		return data
	}
}
