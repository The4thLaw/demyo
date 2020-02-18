import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'

/**
 * API service for Derivative Types.
 */
class DerivativeTypeService extends AbstractModelService {
	constructor() {
		super('derivativeTypes/')
	}

	/**
	 * Finds how many Derivatives use the given type.
	 * @param {Number} id The Derivative Type ID
	 */
	countDerivatives(id) {
		return axiosGet(`derivativeTypes/${id}/derivatives/count`, 0)
	}
}

export default new DerivativeTypeService()
