import { axiosGet, axiosPost } from '@/helpers/axios'
import AbstractModelService from './abstract-model-service'
import readerService from './reader-service'

/**
 * API service for Albums.
 */
class AlbumService extends AbstractModelService<Album> {
	constructor() {
		super('albums/', {
			// Publishers are mandatory but could be missing from album templates
			fillMissingObjects: ['series', 'publisher', 'collection', 'binding', 'cover', 'universe'],
			fillMissingArrays: [
				'writers', 'artists', 'colorists', 'inkers', 'translators', 'coverArtists', 'tags', 'genres',
				'images', 'prices'
			],
			sanitizeArrays: [
				'writers', 'artists', 'colorists', 'inkers', 'translators', 'coverArtists', 'tags', 'genres',
				'images'
			]
		})
	}

	async findForIndex(filter?: AlbumFilter): Promise<Album[]> {
		if (filter) {
			return axiosPost(this.basePath + 'index/filtered', filter, [])
		}
		return axiosGet(this.basePath, [])
	}

	async save(model: Album): Promise<number> {
		const promise = super.save(model)

		// Saving an album may impact the reading list, etc. We should reload them when the save is done
		promise.then((id) => {
			void readerService.loadCurrentReaderLists()
			return id
		}, (err: unknown) => {
			console.log('Failed to reload the reading list', err)
		})

		return promise
	}

	/**
	 * Saves / Commits the images uploaded through FilePond to the current Album.
	 *
	 * @param modelId The Album ID.
	 * @param filePondMainImage The image ID from FilePond for the cover
	 * @param filePondOtherImages The image IDs from FilePond
	 * @return true if saving was successful.
	 */
	async saveFilepondImages(modelId: number, filePondMainImage: string,
		filePondOtherImages: string[]): Promise<boolean> {
		return axiosPost(`${this.basePath}${modelId}/images`,
			{ filePondMainImage, filePondOtherImages }, false)
	}

	/**
	 * Finds how many Derivatives use the given Album.
	 * @param {Number} id The Album ID
	 */
	async countDerivatives(id: number): Promise<number> {
		return axiosGet(`${this.basePath}${id}/derivatives/count`, 0)
	}
}

export default new AlbumService()
