import AbstractModelService from './abstract-model-service'
import { axiosGet, axiosPost } from '@/helpers/axios'

/**
 * API service for Derivatives.
 */
class DerivativeService extends AbstractModelService {
	constructor() {
		super('derivatives/')
	}

	findForIndex(filter) {
		if (filter) {
			return axiosPost(this.basePath + 'index/filtered', filter, [])
		}
		return axiosGet(this.basePath, [])
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

	saveFilepondImages(modelId, imageIds) {
		return axiosPost(`${this.basePath}${modelId}/images`, { filePondOtherImages: imageIds }, false)
	}
}

export default new DerivativeService()
