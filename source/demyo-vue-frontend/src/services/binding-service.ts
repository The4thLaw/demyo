import { axiosGet } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Bindings.
 */
class BindingService extends AbstractModelService<Binding> {
	constructor() {
		super('bindings/')
	}

	/**
	 * Finds how many Albums use the given Binding.
	 * @param id The Binding ID
	 */
	async countAlbums(id: number): Promise<number> {
		return axiosGet(`bindings/${id}/albums/count`, 0)
	}
}

export default new BindingService()
