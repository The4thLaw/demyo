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
		return axiosGet(`${this.basePath}/${seriesId}/albums`, [])
	}
}

export default new SeriesService()
