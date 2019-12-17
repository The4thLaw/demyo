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

	/**
	 * Detects non-registered images currently on the disk.
	 * @return {Promise<String[]>} The image paths
	 */
	detectDiskImages() {
		// TODO: Remove the async/await in service functions and change JSDoc.
		// They are useless unless we want to do something with the awaited value *in the service*.
		// The fact that the function is async means we return a Promise anyway.
		// A bit of a stupid mistake, really...
		return axiosGet('/images/detect')
	}
}

export default new ImageService()
