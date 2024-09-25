import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Derivative Types.
 */
class DerivativeTypeService extends AbstractModelService<DerivativeType> {
	constructor() {
		super('derivativeTypes/')
	}

	/**
	 * Finds how many Derivatives use the given type.
	 * @param id The Derivative Type ID
	 */
	countDerivatives(id: number): Promise<number> {
		return axiosGet(`derivativeTypes/${id}/derivatives/count`, 0)
	}
}

export default new DerivativeTypeService()
