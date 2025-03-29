import AlbumView from '@/pages/albums/AlbumView.vue'
import { createTestingPinia } from '@pinia/testing'
import { RouterLinkStub, shallowMount } from '@vue/test-utils'
import { describe, vi } from 'vitest'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

vi.mock('@/i18n', () => ({
	loadLocaleMessages: () => ({})
}))

const vuetify = createVuetify({
	components,
	directives
})

// Prevent HTTP calls to the API, we'll mock what we have to using the data directly
vi.mock('@/services/album-service', () => ({
	default: {
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
	}
}))

global.ResizeObserver = require('resize-observer-polyfill')

function createWrapper() {
	return shallowMount(AlbumView, {
		global: {
			plugins: [vuetify,
				createTestingPinia({
					stubActions: false
				})
			],
			mocks: {
				$route: {
					params: {
						id: 42
					}
				},
				$t: s => s
			},
			stubs: {
				RouterLink: RouterLinkStub
			}
		}
	})
}

function createAlbum(fields = {}) {
	return {
		id: 42,
		series: {},
		publisher: {},
		binding: {},
		collection: {},
		...fields
	}
}

describe('AlbumView.vue', () => {
	it('Check if there are authors', () => {
		const wrapper = createWrapper()
		for (const prop of ['writers', 'artists', 'colorists', 'inkers', 'translators']) {
			// No authors by default
			expect(wrapper.vm.hasAuthors).toBeFalsy()

			// Set one type of author and check the condition
			const album = createAlbum()
			album[prop] = [{ id: 42, name: 'Foo' }]
			wrapper.setData({ album })
			expect(wrapper.vm.hasAuthors).toBeTruthy()

			// Empty and check again
			album[prop] = []
			wrapper.setData({ album })
			expect(wrapper.vm.hasAuthors).toBeFalsy()
		}
	})

	it('Check if there are prices', () => {
		const wrapper = createWrapper()
		expect(wrapper.vm.hasPrices).toBeFalsy()

		const album = createAlbum({ prices: [{}] })
		wrapper.setData({ album })
		expect(wrapper.vm.hasPrices).toBeTruthy()
	})

	it('Check if there are images', () => {
		const wrapper = createWrapper()
		expect(wrapper.vm.hasImages).toBeFalsy()

		const album = createAlbum({ images: [{}] })
		wrapper.setData({ album })
		expect(wrapper.vm.hasImages).toBeTruthy()
	})

	it('Compute the size specifications', () => {
		const wrapper = createWrapper()
		expect(wrapper.vm.sizeSpec).toBeFalsy()

		const album = createAlbum({
			width: 240,
			height: 320
		})
		wrapper.setData({ album })
		expect(wrapper.vm.sizeSpec).toBe('240 x 320')
	})

	it('Build a query to add a derivative', () => {
		const wrapper = createWrapper()
		const album = createAlbum()
		wrapper.setData({ album })
		expect(wrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42
		})
		album.series = { id: 1337 }
		wrapper.setData({ album })
		expect(wrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42,
			toSeries: 1337
		})
		album.artists = [
			{ id: 1002 },
			{ id: 1001 }
		]
		wrapper.setData({ album })
		expect(wrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42,
			toSeries: 1337
		})
		album.artists = [{ id: 1003 }]
		wrapper.setData({ album })
		expect(wrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42,
			toSeries: 1337,
			toArtist: 1003
		})
	})
})
