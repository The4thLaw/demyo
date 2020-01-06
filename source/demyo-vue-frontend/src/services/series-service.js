import AbstractModelService from './abstract-model-service'

/**
 * API service for Series.
 */
class SeriesService extends AbstractModelService {
	constructor() {
		super('series/')
	}
}

export default new SeriesService()
