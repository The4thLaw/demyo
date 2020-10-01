import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'

/**
 * API service for Derivative Sources.
 */
class DerivativeSourceService extends AbstractModelService {
	constructor() {
		super('derivativeSources/', {
			sanitizeHtml: ['history']
		})
	}

	/**
	 * Finds how many Derivatives use the given source.
	 * @param {Number} id The Derivative Source ID
	 */
	countDerivatives(id) {
		return axiosGet(`derivativeSources/${id}/derivatives/count`, 0)
	}
}

export default new DerivativeSourceService()
