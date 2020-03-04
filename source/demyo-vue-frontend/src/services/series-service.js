import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Series.
 */
class SeriesService extends AbstractModelService {
	constructor() {
		super('series/')
	}

	findAlbumsForList(seriesId) {
		if (!seriesId) {
			seriesId = 'none'
		}
		return axiosGet(`${this.basePath}${seriesId}/albums`, [])
	}

	/**
	 * Finds how many Derivatives use the given series.
	 * @param {Number} id The Series ID
	 */
	countDerivatives(id) {
		return axiosGet(`${this.basePath}${id}/derivatives/count`, 0)
	}
}

export default new SeriesService()
