import AbstractModelService from './abstract-model-service'
import { axiosGet, axiosPost } from '@/helpers/axios'

/**
 * API service for Images.
 */
class ImageService extends AbstractModelService {
	constructor() {
		super('images/')
	}

	/**
	 * Finds the Models which depend on this image.
	 * @param {Number} id The Image ID
	 */
	getImageDependencies(id) {
		return axiosGet(`images/${id}/dependencies`, {})
	}

	/**
	 * Detects non-registered images currently on the disk.
	 * @return {Promise<String[]>} The image paths.
	 */
	detectDiskImages() {
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
