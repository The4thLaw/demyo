import AbstractModelService from './abstract-model-service'
import readerService from './reader-service'
import { axiosGet, axiosPost } from '@/helpers/axios'

/**
 * API service for Albums.
 */
class AlbumService extends AbstractModelService {
	constructor() {
		super('albums/', {
			fillMissingObjects: ['series', 'collection', 'cover'],
			fillMissingArrays: ['writers', 'artists', 'colorists', 'inkers', 'translators', 'tags', 'images', 'prices']
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

	/**
	 * Finds how many Derivatives use the given Album.
	 * @param {Number} id The Album ID
	 */
	countDerivatives(id) {
		return axiosGet(`${this.basePath}${id}/derivatives/count`, 0)
	}
}

export default new AlbumService()
