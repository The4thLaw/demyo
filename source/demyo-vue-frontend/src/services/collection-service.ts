import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Bindings.
 */
class CollectionService extends AbstractModelService<Collection> {
	constructor() {
		super('collections/', {
			fillMissingObjects: ['logo'],
			sanitizeHtml: ['history']
		})
	}

	/**
	 * Finds how many Albums use the given Collection.
	 * @param id The Collection ID
	 */
	async countAlbums(id: number): Promise<number> {
		return axiosGet(`collections/${id}/albums/count`, 0)
	}
}

export default new CollectionService()
