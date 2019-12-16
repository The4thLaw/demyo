import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'

/**
 * API service for Authors.
 */
class ImageService extends AbstractModelService {
	constructor() {
		super('images/')
	}

	/**
	 * Finds the Models which depend on this image.
	 * @param {Number} id The Image ID
	 */
	async getImageDependencies(id) {
		let data = await axiosGet(`images/${id}/dependencies`, {})
		return data
	}
}

export default new ImageService()
