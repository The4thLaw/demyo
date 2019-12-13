import AbstractModelService from './abstract-model-service'

/**
 * API service for Authors.
 */
class DerivativeService extends AbstractModelService {
	constructor() {
		super('derivatives/')
	}
}

export default new DerivativeService()
