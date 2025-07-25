import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'
import albumService from './album-service'

/**
 * API service for Series.
 */
class SeriesService extends AbstractModelService<Series> {
	constructor() {
		super('series/', {
			fillMissingObjects: ['universe'],
			sanitizeHtml: ['summary', 'comment']
		})
	}

	async findAlbumsForList(seriesId?: number | 'none'): Promise<Album[]> {
		seriesId ??= 'none'
		return axiosGet(`${this.basePath}${seriesId}/albums`, [])
	}

	/**
	 * Gets the template for a new Album in a Series.
	 * @param seriesId The Series ID
	 */
	async getAlbumTemplate(seriesId: number): Promise<Album> {
		const template: Album = await axiosGet(`${this.basePath}${seriesId}/albums/template`, {})
		return albumService.fillMissingData(template)
	}

	/**
	 * Finds how many Derivatives use the given series.
	 * @param id The Series ID
	 */
	async countDerivatives(id: number): Promise<number> {
		return axiosGet(`${this.basePath}${id}/derivatives/count`, 0)
	}

	/**
	 * Finds the Universe of a Series
	 * @param id The Series ID
	 * @returns The Universe, if any
	 */
	async getUniverse(id: number): Promise<Universe | undefined> {
		return axiosGet(`${this.basePath}${id}/universe`)
	}
}

export default new SeriesService()
