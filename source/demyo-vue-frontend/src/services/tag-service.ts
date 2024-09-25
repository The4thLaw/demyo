import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Albums.
 */
class TagService extends AbstractModelService<Tag> {
	constructor() {
		super('tags/')
	}

	/**
	 * Finds how many Albums use the given Tag.
	 * @param id The Tag ID
	 */
	countAlbums(id: number): Promise<number> {
		return axiosGet(`tags/${id}/albums/count`, 0)
	}
}

export default new TagService()
