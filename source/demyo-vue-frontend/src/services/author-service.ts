import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Authors.
 */
class AuthorService extends AbstractModelService<Author> {
	constructor() {
		super('authors/', {
			fillMissingObjects: ['portrait', 'pseudonymOf'],
			sanitizeHtml: ['biography']
		})
	}

	/**
	 * Finds the real authors.
	 */
	async findRealForIndex(): Promise<Author[]> {
		return axiosGet('authors/real', [])
	}

	/**
	 * Finds the real authors, applying the list view.
	 */
	async findRealForList(): Promise<Author[]> {
		return axiosGet('authors/real', { view: 'minimal' }, [])
	}

	/**
	 * Finds the Albums on which an Author has worked.
	 * @param id The Author ID
	 */
	async getAuthorAlbums(id: number): Promise<AuthorAlbums> {
		return axiosGet(`authors/${id}/albums`, {})
	}

	/**
	 * Finds how many Derivatives use the given artist.
	 * @param {Number} id The Author ID
	 */
	async countDerivatives(id: number): Promise<number> {
		return axiosGet(`authors/${id}/derivatives/count`, 0)
	}
}

export default new AuthorService()
