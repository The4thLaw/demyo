import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Publishers.
 */
class PublisherService extends AbstractModelService<Publisher> {
	constructor() {
		super('publishers/', {
			fillMissingObjects: ['logo'],
			sanitizeHtml: ['history']
		})
	}

	/**
	 * Finds the Collections belonging to a Publisher.
	 * @param publisherId The Publisher ID
	 */
	async findCollectionsForList(publisherId: number | undefined): Promise<Collection[]> {
		if (!publisherId) {
			return Promise.resolve([])
		}
		return axiosGet(`${this.basePath}${publisherId}/collections`, [])
	}

	/**
	 * Finds how many Albums use the given Publisher.
	 * @param id The Publisher ID
	 */
	async countAlbums(id: number): Promise<number> {
		return axiosGet(`publishers/${id}/albums/count`, 0)
	}
}

export default new PublisherService()
