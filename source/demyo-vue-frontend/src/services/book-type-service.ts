import { axiosGet, axiosPost } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Book Types.
 */
class BookTypeService extends AbstractModelService<BookType> {
	constructor() {
		super('bookTypes/', {
			fillMissingArrays: ['structuredFieldConfig']
		})
	}

	/**
	 * Checks if book type management has been enabled
	 */
	async isManagementEnabled(): Promise<boolean> {
		return axiosGet('bookTypes/management/enabled', false)
	}

	async enableManagement(): Promise<void> {
		await axiosPost('bookTypes/management/enable', undefined)
	}

	/**
	 * Finds how many Albums use the given Book Type.
	 * @param id The Book Type ID
	 */
	async countAlbums(id: number): Promise<number> {
		return axiosGet(`bookTypes/${id}/albums/count`, 0)
	}
}

export default new BookTypeService()
