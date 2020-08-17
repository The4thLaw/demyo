import i18n, { switchLanguage } from '@/i18n'
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
			reader = await axiosGet(this.basePath + reader.id, null)
		} else {
			reader = await axiosGet(this.basePath + 'autoSelect', null)
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

	mayDeleteReader() {
		return axiosGet(`${this.basePath}mayDelete`, false)
	}

	async setCurrentReader(reader) {
		console.log('Setting reader in store', reader)
		let storeProm = store.dispatch('reader/setCurrentReader', reader)
		localStorage.setItem('currentReader', JSON.stringify(reader))
		if (reader.configuration.language) {
			switchLanguage(reader.configuration.language)
		}
		await storeProm
	}

	loadCurrentReaderLists() {
		let reader = store.state.reader.currentReader
		return this.loadLists(reader)
	}

	async loadLists(reader) {
		let lists = await axiosGet(`${this.basePath}${reader.id}/lists`)
		console.log('Loaded reader lists', lists)
		store.dispatch('reader/setReaderLists', lists)
	}

	findFavouriteAlbums(readerId) {
		return axiosGet(`${this.basePath}${readerId}/favourites/albums`, [])
	}

	findReadingList(readerId) {
		return axiosGet(`${this.basePath}${readerId}/readingList/albums`, [])
	}

	addFavouriteSeries(item) {
		return this.addOrRemoveListItem('addFavouriteSeries', axiosPost, 'favourites', 'series', item,
			'readers.confirm.favourite.add')
	}

	removeFavouriteSeries(item) {
		return this.addOrRemoveListItem('removeFavouriteSeries', axiosDelete, 'favourites', 'series', item,
			'readers.confirm.favourite.remove')
	}

	addFavouriteAlbum(item) {
		return this.addOrRemoveListItem('addFavouriteAlbum', axiosPost, 'favourites', 'albums', item,
			'readers.confirm.favourite.add')
	}

	removeFavouriteAlbum(item) {
		return this.addOrRemoveListItem('removeFavouriteAlbum', axiosDelete, 'favourites', 'albums', item,
			'readers.confirm.favourite.remove')
	}

	addToReadingList(item) {
		return this.addOrRemoveListItem('addToReadingList', axiosPost, 'readingList', 'albums', item,
			'readers.confirm.readingList.add')
	}

	removeFromReadingList(item) {
		return this.addOrRemoveListItem('removeFromReadingList', axiosDelete, 'readingList', 'albums', item,
			'readers.confirm.readingList.remove')
	}

	async addSeriesToReadingList(item) {
		let reader = store.state.reader.currentReader
		let newList = await axiosPost(`${this.basePath}${reader.id}/readingList/series/${item}`, [])
		store.dispatch('ui/showSnackbar', i18n.t('readers.confirm.readingList.add'))
		return store.dispatch('reader/setReadingList', newList)
	}

	/** @private */
	async addOrRemoveListItem(storeAction, handler, listType, itemType, id, confirmLabel) {
		let reader = store.state.reader.currentReader
		let success = await handler(`${this.basePath}${reader.id}/${listType}/${itemType}/${id}`, false)
		if (success) {
			store.dispatch('ui/showSnackbar', i18n.t(confirmLabel))
			return store.dispatch('reader/' + storeAction, id)
		}
		return Promise.resolve()
	}
}

export default new ReaderService()
