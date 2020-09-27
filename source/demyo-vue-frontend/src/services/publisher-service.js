import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'

/**
 * API service for Publishers.
 */
class PublisherService extends AbstractModelService {
	constructor() {
		super('publishers/', {
			fillMissingObjects: ['logo'],
			sanitizeHtml: ['history']
		})
	}

	/**
	 * Finds how many Derivatives use the given artist.
	 * @param {Number} id The Derivative Type ID
	 */
	countDerivatives(id) {
		return axiosGet(`authors/${id}/derivatives/count`, 0)
	}
}

export default new PublisherService()
