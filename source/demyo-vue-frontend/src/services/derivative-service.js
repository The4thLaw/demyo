import AbstractModelService from './abstract-model-service'

/**
 * API service for Derivatives.
 */
class DerivativeService extends AbstractModelService {
	constructor() {
		super('derivatives/')
	}

	save(model) {
		// Sanitize the model before saving
		if (!model.series.id) {
			model.series = {}
		}
		if (!model.album.id) {
			model.album = {}
		}
		if (!model.artist.id) {
			model.artist = {}
		}
		if (!model.source.id) {
			model.source = {}
		}
		return super.save(model)
	}
}

export default new DerivativeService()
