import { axiosGet, axiosPost } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'

/**
 * API service for Book Types.
 */
class BookTypeService extends AbstractModelService<BookType> {
	constructor() {
		super('bookTypes/')
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
}

export default new BookTypeService()
