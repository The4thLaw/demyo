import AbstractModelService from './abstract-model-service'
import readerService from './reader-service'
import { axiosGet, axiosPost } from '@/helpers/axios'

/**
 * API service for Albums.
 */
class AlbumService extends AbstractModelService {
	constructor() {
		super('albums/', {
			fillMissingObjects: ['series', 'collection']
		})
	}

	findForIndex(filter) {
		if (filter) {
			return axiosPost(this.basePath + 'index/filtered', filter, [])
		}
		return axiosGet(this.basePath, [])
	}

	save(model) {
		let promise = super.save(model)

		// Saving an album may impact the reading list, etc. We should reload them when the save is done
		promise.then(() => {
			readerService.loadCurrentReaderLists()
		})

		return promise
	}

	saveFilepondImages(modelId, coverId, otherImageIds) {
		return axiosPost(`${this.basePath}${modelId}/images`,
			{ filePondMainImage: coverId, filePondOtherImages: otherImageIds }, false)
	}
}

export default new AlbumService()
