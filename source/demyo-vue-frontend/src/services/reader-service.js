import AbstractModelService from './abstract-model-service'
import { axiosGet, axiosPost, axiosPut, axiosDelete } from '@/helpers/axios'
import store from '@/store'

/**
 * API service for Readers.
 */
class ReaderService extends AbstractModelService {
	constructor() {
		super('readers/')
	}

	async init() {
		console.debug('Initializing reader')

		// Check if we already have a Reader in the local storage
		let reader = localStorage.getItem('currentReader')

		if (reader) {
			reader = JSON.parse(reader)
		} else {
			reader = await axiosGet(this.basePath + '/autoSelect', null)
		}

		if (reader) {
			await setCurrentReader(reader)
		}

		console.log('Reader is initialized to', reader)
	}

	getCurrentReader() {
		return store.state.reader.currentReader
	}

	async setCurrentReader(reader) {
		console.log('Setting reader in store', reader)
		let storeProm = store.dispatch('reader/setCurrentReader', reader)
		localStorage.setItem('currentReader', JSON.stringify(reader))
		await storeProm
	}
}

export default new ReaderService()
