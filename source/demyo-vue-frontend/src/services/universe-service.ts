import { axiosGet } from '@/helpers/axios'
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
}

export default new UniverseService()
