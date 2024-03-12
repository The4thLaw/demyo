import { axiosDelete, axiosGet, axiosPost } from '@/helpers/axios'
import { loadReaderFromLocalStorage, saveReaderToLocalStorage } from '@/helpers/reader'
import i18n, { switchLanguage } from '@/i18n'
import { defaultLanguage } from '@/myenv'
import { useReaderStore } from '@/stores/reader'
import { useUiStore } from '@/stores/ui'
import AbstractModelService from './abstract-model-service'

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
		let reader = loadReaderFromLocalStorage()

		if (reader) {
			console.debug('Restoring Reader from local storage...')
			// Already set it in store, it could be used temporarily at least
			this.setCurrentReader(reader, false)
			// Revalidate the reader. Who knows, it could have been deleted in the mean time
			reader = await axiosGet(this.basePath + reader.id, null)
		} else {
			reader = await axiosGet(this.basePath + 'autoSelect', null)
		}

		if (reader) {
			await this.setCurrentReader(reader, false)
			// Manually load the lists because we requested no reload because we just loaded one
			this.loadLists(reader)
			console.log('Reader is initialized to', reader)
			useUiStore().showSnackbar(i18n.t('core.reader.welcome', { reader: reader.name }))
		} else {
			console.log('Cannot select a reader automatically, prompting user...')
			useReaderStore().requireReaderSelection()
		}
	}

	/**
	 * Finds a Model by its ID.
	 * @param {Number} id The Model ID
	 * @return {Promise<any>} The Model
	 */
	async findById(id) {
		const model = await super.findById(id)
		this.setDefaultConfiguration(model)
		return model
	}

	async save(model) {
		const superReturn = await super.save(model)

		// If we just saved the current reader, we should reload it from the server to have a fresh copy
		if (model.id === useReaderStore().currentReader.id) {
			console.log('The current reader was just edited, reloading it from the server')
			this.setCurrentReader(model)
		}

		return superReturn
	}

	/**
	 * Sets a default configuration to the provided Reader. Only missing configuration values are set.
	 * @param {any} reader The Reader to configure
	 */
	setDefaultConfiguration(reader) {
		reader.configuration = Object.assign({
			language: defaultLanguage,
			pageSizeForText: parseInt(import.meta.env.VITE_DEF_CFG_RDR_PAGESIZEFORTEXT, 10),
			pageSizeForCards: parseInt(import.meta.env.VITE_DEF_CFG_RDR_PAGESIZEFORCARDS, 10),
			subItemsInCardIndex: parseInt(import.meta.env.VITE_DEF_CFG_RDR_SUBITEMSINCARDINDEX, 10),
			pageSizeForImages: parseInt(import.meta.env.VITE_DEF_CFG_RDR_PAGESIZEFORIMAGES, 10)
		}, reader.configuration)
	}

	mayDeleteReader() {
		return axiosGet(`${this.basePath}mayDelete`, false)
	}

	/**
	 * Sets the current reader in the store and local storage.
	 *
	 * @param {any} reader The reader to set
	 * @param {Boolean} reload Whether to reload fresh data from the server
	 */
	async setCurrentReader(reader, reload = true) {
		let listLoadProm

		if (reload) {
			console.log('Loading a fresh copy of the Reader from the server...')
			reader = await this.findById(reader.id)
			listLoadProm = this.loadLists(reader)
		} else {
			// Done by findByID in the previous branch, but we don't want to do it twice
			this.setDefaultConfiguration(reader)
		}

		console.log('Setting reader in store', reader)
		useReaderStore().setCurrentReader(reader)
		saveReaderToLocalStorage(reader)
		if (reader.configuration.language) {
			switchLanguage(reader.configuration.language)
		}

		if (listLoadProm) {
			await listLoadProm
		}
	}

	loadCurrentReaderLists() {
		const reader = useReaderStore().currentReader
		return this.loadLists(reader)
	}

	async loadLists(reader) {
		const lists = await axiosGet(`${this.basePath}${reader.id}/lists`)
		console.log('Loaded reader lists', lists)
		useReaderStore().setReaderLists(lists)
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
		const readerStore = useReaderStore()
		const reader = readerStore.currentReader
		const newList = await axiosPost(`${this.basePath}${reader.id}/readingList/series/${item}`, [])
		useUiStore().showSnackbar(i18n.t('readers.confirm.readingList.add'))
		readerStore.setReadingList(newList)
	}

	/** @private */
	async addOrRemoveListItem(storeAction, handler, listType, itemType, id, confirmLabel) {
		const readerStore = useReaderStore()
		const reader = readerStore.currentReader
		const success = await handler(`${this.basePath}${reader.id}/${listType}/${itemType}/${id}`, false)
		if (success) {
			useUiStore().showSnackbar(i18n.t(confirmLabel))
			readerStore[storeAction](id)
		}
	}
}

export default new ReaderService()
