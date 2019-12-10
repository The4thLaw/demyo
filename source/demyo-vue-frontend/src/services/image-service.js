import AbstractModelService from './abstract-model-service'

/**
 * API service for Authors.
 */
class ImageService extends AbstractModelService {
	constructor() {
		super('images/')
	}
}

export default new ImageService()
