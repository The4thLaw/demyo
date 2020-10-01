import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'

/**
 * API service for Bindings.
 */
class CollectionService extends AbstractModelService {
	constructor() {
		super('collections/')
	}

	/**
	 * Finds how many Albums use the given Collection.
	 * @param {Number} id The Collection ID
	 */
	countAlbums(id) {
		return axiosGet(`collections/${id}/albums/count`, 0)
	}
}

export default new CollectionService()
