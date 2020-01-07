import AbstractModelService from './abstract-model-service'

/**
 * API service for Albums.
 */
class AlbumService extends AbstractModelService {
	constructor() {
		super('albums/')
	}
}

export default new AlbumService()
