import AbstractModelService from './abstract-model-service'

/**
 * API service for Derivative Sources.
 */
class DerivativeSourceService extends AbstractModelService {
	constructor() {
		super('derivativeSources/')
	}
}

export default new DerivativeSourceService()
