import bookTypeService from '@/services/book-type-service'
import { defineStore } from 'pinia'

export const useRefDataStore = defineStore('ref-data', {
	state: () => ({
		bookTypeManagement: false,
		bookTypes: [] as BookType[]
	}),

	actions: {
		async refreshBookTypeManagement() {
			this.bookTypeManagement = await bookTypeService.isManagementEnabled()
		}
	}
})
