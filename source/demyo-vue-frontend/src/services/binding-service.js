import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'

/**
 * API service for Bindings.
 */
class BindingService extends AbstractModelService {
	constructor() {
		super('bindings/')
	}

	/**
	 * Finds how many Albums use the given Binding.
	 * @param {Number} id The Binding ID
	 */
	countAlbums(id) {
		return axiosGet(`bindings/${id}/albums/count`, 0)
	}
}

export default new BindingService()
