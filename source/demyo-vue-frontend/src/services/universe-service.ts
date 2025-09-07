import { axiosGet, axiosPost } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Universes.
 */
class UniverseService extends AbstractModelService<Universe> {
	constructor() {
		super('universes/', {
			fillMissingObjects: ['logo'],
			fillMissingArrays: ['images'],
			sanitizeArrays: ['images'],
			sanitizeHtml: ['description']
		})
	}

	/**
	 * Finds the Albums which are part of a Universe, either directly or through its Series.
	 * @param id The Universe ID
	 */
	async getUniverseContents(id: number): Promise<Album[]> {
		return axiosGet(`universes/${id}/albums`, [])
	}

	/**
	 * Saves / Commits the images uploaded through FilePond to the current Universe.
	 *
	 * @param modelId The Album ID.
	 * @param filePondMainImage The image ID from FilePond for the logo
	 * @param filePondOtherImages The image IDs from FilePond
	 * @return true if saving was successful.
	 */
	async saveFilepondImages(modelId: number, filePondMainImage: string,
		filePondOtherImages: string[]): Promise<boolean> {
		return axiosPost(`${this.basePath}${modelId}/images`,
			{ filePondMainImage, filePondOtherImages }, false)
	}
}

export default new UniverseService()
