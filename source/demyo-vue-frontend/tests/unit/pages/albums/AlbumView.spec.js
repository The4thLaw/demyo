import { createLocalVue, shallowMount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import VueRouter from 'vue-router'

jest.mock('@/i18n', () => ({
	loadLocaleMessages: () => ({})
}))
// We will use setData to make the checks
jest.mock('@/services/album-service', () => ({
	countDerivatives: () => 0,
	findById: () => {
		return {
			id: NaN,
			series: {},
			binding: {},
			publisher: {},
			collection: {}
		}
	}
}))
// eslint-disable-next-line import/first
import AlbumView from '@/pages/albums/AlbumView.vue'

// TODO: Vue 3: fix unit tests. See https://vitest.dev/guide/migration.html#migrating-from-jest maybe
const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()

describe('FormActions.vue', () => {
	let shallowWrapper, baseAlbum

	beforeEach(() => {
		setActivePinia(createPinia())

		shallowWrapper = shallowMount(AlbumView, {
			localVue,
			router,
			mocks: {
				$tc: s => s
			}
		})

		baseAlbum = {
			id: 42,
			series: {},
			publisher: {},
			binding: {},
			collection: {}
		}
	})

	it('Properly checks if there are authors', () => {
		for (const prop of ['writers', 'artists', 'colorists', 'inkers', 'translators']) {
			// No authors by default
			expect(shallowWrapper.vm.hasAuthors).toBeFalsy()
			// Set one author and check the condition
			baseAlbum[prop] = [{ id: 42, name: 'Foo' }]
			shallowWrapper.setData({ album: baseAlbum })
			expect(shallowWrapper.vm.hasAuthors).toBeTruthy()
			// Reset for next loop (and check)
			baseAlbum[prop] = []
			shallowWrapper.setData({ album: baseAlbum })
			expect(shallowWrapper.vm.hasAuthors).toBeFalsy()
		}
	})

	it('Properly checks if there are prices', () => {
		expect(shallowWrapper.vm.hasPrices).toBeFalsy()
		baseAlbum.prices = [{}]
		shallowWrapper.setData({ album: baseAlbum })
		expect(shallowWrapper.vm.hasPrices).toBeTruthy()
	})

	it('Properly checks if there are images', () => {
		expect(shallowWrapper.vm.hasImages).toBeFalsy()
		baseAlbum.images = [{}]
		shallowWrapper.setData({ album: baseAlbum })
		expect(shallowWrapper.vm.hasImages).toBeTruthy()
	})

	it('Computes the size specifications', () => {
		expect(shallowWrapper.vm.sizeSpec).toBeFalsy()
		baseAlbum.width = 240
		baseAlbum.height = 320
		shallowWrapper.setData({ album: baseAlbum })
		expect(shallowWrapper.vm.sizeSpec).toBe('240 x 320')
	})

	it('Builds a query to add a derivative', () => {
		shallowWrapper.setData({ album: baseAlbum })
		expect(shallowWrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42
		})
		baseAlbum.series = { id: 1337 }
		shallowWrapper.setData({ album: baseAlbum })
		expect(shallowWrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42,
			toSeries: 1337
		})
		baseAlbum.artists = [
			{ id: 1002 },
			{ id: 1001 }
		]
		shallowWrapper.setData({ album: baseAlbum })
		expect(shallowWrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42,
			toSeries: 1337
		})
		baseAlbum.artists = [{ id: 1003 }]
		shallowWrapper.setData({ album: baseAlbum })
		expect(shallowWrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42,
			toSeries: 1337,
			toArtist: 1003
		})
	})
})
