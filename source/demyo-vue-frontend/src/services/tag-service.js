import AbstractModelService from './abstract-model-service'

/**
 * API service for Albums.
 */
class TagService extends AbstractModelService {
	constructor() {
		super('tags/')
	}
}

export default new TagService()
