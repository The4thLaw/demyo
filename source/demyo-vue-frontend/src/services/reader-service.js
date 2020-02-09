import AbstractModelService from './abstract-model-service'
import { axiosGet } from '@/helpers/axios'
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
		const readerStr = localStorage.getItem('currentReader')
		let reader

		if (readerStr) {
			console.debug('Restoring Reader from local storage...')
			reader = JSON.parse(readerStr)
			// Already set it in store, it could be used temporarily at least
			this.setCurrentReader(reader)
			// Revalidate the reader. Who knows, it could have been deleted in the mean time
			reader = await axiosGet(this.basePath + '/' + reader.id, null)
		} else {
			reader = await axiosGet(this.basePath + '/autoSelect', null)
		}

		if (reader) {
			await this.setCurrentReader(reader)
			console.log('Reader is initialized to', reader)
		} else {
			console.log('Cannot select a reader automatically, prompting user...')
			store.dispatch('reader/requireReaderSelection')
		}
	}

	async setCurrentReader(reader) {
		console.log('Setting reader in store', reader)
		let storeProm = store.dispatch('reader/setCurrentReader', reader)
		localStorage.setItem('currentReader', JSON.stringify(reader))
		await storeProm
	}
}

export default new ReaderService()
