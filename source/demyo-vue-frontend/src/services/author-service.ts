import { axiosGet, axiosPost } from '@/helpers/axios'
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
	 * Finds the genres the Author participates to.
	 * @param id The Author ID
	 */
	async getAuthorGenres(id: number): Promise<Taxon[]> {
		return axiosGet(`authors/${id}/genres`, {})
	}

	/**
	 * Finds how many Derivatives use the given artist.
	 * @param {Number} id The Author ID
	 */
	async countDerivatives(id: number): Promise<number> {
		return axiosGet(`authors/${id}/derivatives/count`, 0)
	}

	/**
	 * Saves / Commits the images uploaded through FilePond to the current Author.
	 *
	 * @param modelId The Author ID.
	 * @param filePondMainImage The image ID from FilePond
	 * @return true if saving was successful.
	 */
	async saveFilepondImages(modelId: number, filePondMainImage: string): Promise<boolean> {
		return axiosPost(`${this.basePath}${modelId}/images`, { filePondMainImage }, false)
	}
}

export default new AuthorService()
