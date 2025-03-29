import { axiosGet, axiosPost } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Derivatives.
 */
class DerivativeService extends AbstractModelService<Derivative> {
	constructor() {
		super('derivatives/', {
			fillMissingObjects: ['series', 'album', 'artist', 'source', 'type'],
			fillMissingArrays: ['prices', 'images'],
			sanitizeArrays: ['images'],
			sanitizeObjects: ['series', 'album', 'artist', 'source'],
			sanitizeHtml: ['description']
		})
	}

	findForIndex(filter?: DerivativeFilter, view?: string): Promise<Derivative[]> {
		if (filter) {
			return axiosPost(this.basePath + 'index/filtered', filter, [])
		}
		const params: Record<string, string> = {}
		if (view) {
			params.view = view
		}
		return axiosGet(this.basePath, params, [])
	}

	saveFilepondImages(modelId: number, imageIds: string[]) {
		return axiosPost(`${this.basePath}${modelId}/images`, { filePondOtherImages: imageIds }, false)
	}
}

export default new DerivativeService()
