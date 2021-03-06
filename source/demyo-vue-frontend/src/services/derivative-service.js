import AbstractModelService from './abstract-model-service'
import { axiosGet, axiosPost } from '@/helpers/axios'

/**
 * API service for Derivatives.
 */
class DerivativeService extends AbstractModelService {
	constructor() {
		super('derivatives/', {
			fillMissingObjects: ['series', 'album', 'artist', 'source', 'type'],
			fillMissingArrays: ['prices', 'images'],
			sanitizeArrays: ['images'],
			sanitizeObjects: ['series', 'album', 'artist', 'source'],
			sanitizeHtml: ['description']
		})
	}

	findForIndex(filter, view) {
		if (filter) {
			return axiosPost(this.basePath + 'index/filtered', filter, [])
		}
		const params = {}
		if (view) {
			params.view = view
		}
		return axiosGet(this.basePath, params, [])
	}

	saveFilepondImages(modelId, imageIds) {
		return axiosPost(`${this.basePath}${modelId}/images`, { filePondOtherImages: imageIds }, false)
	}
}

export default new DerivativeService()
