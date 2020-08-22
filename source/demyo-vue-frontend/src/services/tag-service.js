import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'

/**
 * API service for Albums.
 */
class TagService extends AbstractModelService {
	constructor() {
		super('tags/')
	}

	/**
	 * Finds how many Albums use the given Tag.
	 * @param {Number} id The Tag ID
	 */
	countAlbums(id) {
		return axiosGet(`tags/${id}/albums/count`, 0)
	}
}

export default new TagService()
