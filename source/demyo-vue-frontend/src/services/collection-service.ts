import { axiosGet, axiosPost } from '@/helpers/axios'
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

	/**
	 * Saves / Commits the images uploaded through FilePond to the current Collection.
	 *
	 * @param modelId The Collection ID.
	 * @param filePondMainImage The image ID from FilePond
	 * @return true if saving was successful.
	 */
	async saveFilepondImages(modelId: number, filePondMainImage: string): Promise<boolean> {
		return axiosPost(`${this.basePath}${modelId}/images`, { filePondMainImage }, false)
	}
}

export default new CollectionService()
