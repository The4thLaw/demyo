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
	getAuthorAlbums(id) {
		return axiosGet(`authors/${id}/albums`, {})
	}

	/**
	 * Finds how many Derivatives use the given artist.
	 * @param {Number} id The Derivative Type ID
	 */
	countDerivatives(id) {
		return axiosGet(`authors/${id}/derivatives/count`, 0)
	}
}

export default new AuthorService()
