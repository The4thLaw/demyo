import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'

/**
 * API service for Authors.
 */
class AuthorService extends AbstractModelService {
	constructor() {
		super('authors/')
	}

	/**
	 * Finds the Albums on which an Author has worked.
	 * @param {Number} id The Author ID
	 */
	async getAuthorAlbums(id) {
		let data = await axiosGet(`authors/${id}/albums`, {})
		return data
	}
}

export default new AuthorService()
