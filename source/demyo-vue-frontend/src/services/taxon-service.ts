import { axiosGet, axiosPost } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Taxons.
 */
class TaxonService extends AbstractModelService<Taxon> {
	constructor() {
		super('taxons/')
	}

	/**
	 * Finds how many Albums use the given Taxon.
	 * @param id The Taxon ID
	 */
	async countAlbums(id: number): Promise<number> {
		return axiosGet(`taxons/${id}/albums/count`, 0)
	}

	/**
	 * Converts a Taxon from tag to genre or vice-versa.
	 * @param id The Taxon ID
	 */
	async convertType(id: number): Promise<void> {
		return axiosPost(`taxons/${id}/convert`, undefined)
	}
}

export default new TaxonService()
