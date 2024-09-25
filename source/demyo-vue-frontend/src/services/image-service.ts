import { axiosGet, axiosPost } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Images.
 */
class ImageService extends AbstractModelService<Image> {
	constructor() {
		super('images/')
	}

	/**
	 * Finds the Models which depend on this image.
	 * @param id The Image ID
	 */
	getImageDependencies(id: number): Promise<Image> {
		return axiosGet(`images/${id}/dependencies`, {})
	}

	/**
	 * Detects non-registered images currently on the disk.
	 * @return The image paths.
	 */
	detectDiskImages(): Promise<String[]> {
		return axiosGet('images/detect', [])
	}

	/**
	 * Saves a set of detected images.
	 * @param images The paths of the images to add.
	 * @return The added image IDs.
	 */
	saveDiskImages(images: string[]): Promise<number[]> {
		return axiosPost('images/detect', images, [])
	}
}

export default new ImageService()
