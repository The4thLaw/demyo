import AbstractModelService from './abstract-model-service'
import { axiosGet, axiosPost } from '@/helpers/axios'

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
	 * @return {Promise<String[]>} The image paths.
	 */
	detectDiskImages() {
		// TODO: Remove the async/await in service functions and change JSDoc.
		// They are useless unless we want to do something with the awaited value *in the service*.
		// The fact that the function is async means we return a Promise anyway.
		// A bit of a stupid mistake, really...
		return axiosGet('/images/detect')
	}

	/**
	 * Saves a set of detected images.
	 * @param {String[]} images The paths of the images to add.
	 * @return {Promise<Number[]>} The added image IDs.
	 */
	saveDiskImages(images) {
		return axiosPost('/images/detect', images, [])
	}
}

export default new ImageService()
