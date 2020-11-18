import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'

/**
 * API service for Publishers.
 */
class PublisherService extends AbstractModelService {
	constructor() {
		super('publishers/', {
			fillMissingObjects: ['logo'],
			sanitizeHtml: ['history']
		})
	}

	/**
	 * Finds the Collections belonging to a Publisher.
	 * @param {Number} publisherId The Publisher ID
	 */
	findCollectionsForList(publisherId) {
		return axiosGet(`${this.basePath}${publisherId}/collections`, [])
	}

	/**
	 * Finds how many Albums use the given Publisher.
	 * @param {Number} id The Publisher ID
	 */
	countAlbums(id) {
		return axiosGet(`publishers/${id}/albums/count`, 0)
	}
}

export default new PublisherService()
