import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Derivative Sources.
 */
class DerivativeSourceService extends AbstractModelService<DerivativeSource> {
	constructor() {
		super('derivativeSources/', {
			sanitizeHtml: ['history']
		})
	}

	/**
	 * Finds how many Derivatives use the given source.
	 * @param id The Derivative Source ID
	 */
	async countDerivatives(id: number): Promise<number> {
		return axiosGet(`derivativeSources/${id}/derivatives/count`, 0)
	}
}

export default new DerivativeSourceService()
