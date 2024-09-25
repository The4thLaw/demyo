import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'
import albumService from './album-service'

/**
 * API service for Series.
 */
class SeriesService extends AbstractModelService<Series> {
	constructor() {
		super('series/', {
			fillMissingArrays: ['relatedSeries'],
			sanitizeArrays: ['relatedSeries'],
			sanitizeHtml: ['summary', 'comment']
		})
	}

	findAlbumsForList(seriesId: number|'none') {
		if (!seriesId) {
			seriesId = 'none'
		}
		return axiosGet(`${this.basePath}${seriesId}/albums`, [])
	}

	/**
	 * Gets the template for a new Album in a Series.
	 * @param seriesId The Series ID
	 */
	async getAlbumTemplate(seriesId: number) {
		const template = await axiosGet(`${this.basePath}${seriesId}/albums/template`, {})
		return albumService.fillMissingData(template)
	}

	/**
	 * Finds how many Derivatives use the given series.
	 * @param id The Series ID
	 */
	countDerivatives(id: number): Promise<number> {
		return axiosGet(`${this.basePath}${id}/derivatives/count`, 0)
	}
}

export default new SeriesService()