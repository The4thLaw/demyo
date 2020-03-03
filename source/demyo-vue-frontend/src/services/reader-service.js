import i18n from '@/i18n'
import AbstractModelService from './abstract-model-service'
import { axiosGet, axiosPost, axiosDelete } from '@/helpers/axios'
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
			this.loadLists(reader)
			console.log('Reader is initialized to', reader)
			store.dispatch('ui/showSnackbar', i18n.t('core.reader.welcome', { reader: reader.name }))
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

	async loadLists(reader) {
		let lists = await axiosGet(`${this.basePath}/${reader.id}/lists`)
		console.log('Loaded reader lists', lists)
		store.dispatch('reader/setReaderLists', lists)
	}

	addFavouriteSeries(item) {
		return this.addOrRemoveListItem('addFavouriteSeries', axiosPost, 'favourites', 'series', item)
	}

	removeFavouriteSeries(item) {
		return this.addOrRemoveListItem('removeFavouriteSeries', axiosDelete, 'favourites', 'series', item)
	}

	addFavouriteAlbum(item) {
		return this.addOrRemoveListItem('addFavouriteAlbum', axiosPost, 'favourites', 'albums', item)
	}

	removeFavouriteAlbum(item) {
		return this.addOrRemoveListItem('removeFavouriteAlbum', axiosDelete, 'favourites', 'albums', item)
	}

	addToReadingList(item) {
		return this.addOrRemoveListItem('addToReadingList', axiosPost, 'readingList', 'albums', item)
	}

	removeFromReadingList(item) {
		return this.addOrRemoveListItem('removeFromReadingList', axiosDelete, 'readingList', 'albums', item)
	}

	/** @private */
	async addOrRemoveListItem(storeAction, handler, listType, itemType, id) {
		let reader = store.state.reader.currentReader
		let success = await handler(`${this.basePath}/${reader.id}/${listType}/${itemType}/${id}`, false)
		if (success) {
			return store.dispatch('reader/' + storeAction, id)
		}
		return Promise.resolve()
	}
}

export default new ReaderService()
