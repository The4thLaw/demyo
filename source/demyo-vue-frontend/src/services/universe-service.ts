import AbstractModelService from './abstract-model-service'

/**
 * API service for Universes.
 */
class UniverseService extends AbstractModelService<Universe> {
	constructor() {
		super('universes/', {
			fillMissingArrays: ['images'],
			sanitizeArrays: ['images'],
			sanitizeHtml: ['description']
		})
	}
}

export default new UniverseService()
