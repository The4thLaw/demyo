import AbstractModelService from './abstract-model-service'

/**
 * API service for Derivative Types.
 */
class DerivativeTypeService extends AbstractModelService {
	constructor() {
		super('derivativeTypes/')
	}
}

export default new DerivativeTypeService()
