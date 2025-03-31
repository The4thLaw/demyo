// This test is disabled for now as I just can't understand how I'm supposed to test the composable
import AlbumView from '@/pages/albums/AlbumView.vue'
import { createTestingPinia } from '@pinia/testing'
import { RouterLinkStub, shallowMount } from '@vue/test-utils'
import { describe, vi } from 'vitest'
import { ref } from 'vue'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

// Eventually, we could mock this in common setup files, see
// https://stackoverflow.com/a/73424585/109813
vi.mock('@/i18n', () => ({
	loadLocaleMessages: () => ({})
}))

vi.mock('vue-i18n', () => ({
	useI18n: () => ({
		t: (key) => key,
		d: (key) => key
	})
}))

vi.mock('vue-router', () => ({
	useRoute: () => ({
		params: {
		  id: 42
		}
	  }),
	useRouter: vi.fn(() => ({
	  push: () => {}
	}))
}))

const vuetify = createVuetify({
	components,
	directives
})

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
				route: {
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

vi.mock('@/composables/model-view', () => ({
	useSimpleView: () => ({
		model: ref({
			id: undefined,
			series: {},
			publisher: {},
			binding: {},
			collection: {}
		}),
		appTaskMenu: ref(false),
		loading: ref(false),
		deleteModel: () => {},
		loadData: () => {}
	})
}))

describe('AlbumView.vue', () => {
	/** @type VueWrapper */
	let wrapper

	beforeEach(() => {
		vi.resetAllMocks()
		wrapper = createWrapper()
	})

	it('Check if there are authors', () => {
		const wrapper = createWrapper()
		for (const prop of ['writers', 'artists', 'colorists', 'inkers', 'translators']) {
			// No authors by default
			expect(wrapper.vm.hasAuthors).toBeFalsy()

			// Set one type of author and check the condition
			wrapper.vm.album[prop] = [{ id: 42, name: 'Foo' }]
			expect(wrapper.vm.hasAuthors).toBeTruthy()

			// Empty and check again
			wrapper.vm.album[prop] = []
			expect(wrapper.vm.hasAuthors).toBeFalsy()
		}
	})

	it('Check if there are prices', () => {
		expect(wrapper.vm.hasPrices).toBeFalsy()

		wrapper.vm.album.prices = [{}]
		expect(wrapper.vm.hasPrices).toBeTruthy()
	})

	it('Check if there are images', () => {
		expect(wrapper.vm.hasImages).toBeFalsy()

		wrapper.vm.album.images = [{}]
		expect(wrapper.vm.hasImages).toBeTruthy()
	})

	it('Compute the size specifications', () => {
		expect(wrapper.vm.sizeSpec).toBeFalsy()

		wrapper.vm.album.width = 240
		wrapper.vm.album.height = 320
		expect(wrapper.vm.sizeSpec).toBe('240 x 320')
	})

	it('Build a query to add a derivative', () => {
		wrapper.vm.album.id = 42
		expect(wrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42
		})

		wrapper.vm.album.series.id = 1337
		expect(wrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42,
			toSeries: 1337
		})

		wrapper.vm.album.artists = [
			{ id: 1002 },
			{ id: 1001 }
		]
		expect(wrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42,
			toSeries: 1337
		})

		wrapper.vm.album.artists = [{ id: 1003 }]
		expect(wrapper.vm.derivativeQuery).toEqual({
			toAlbum: 42,
			toSeries: 1337,
			toArtist: 1003
		})
	})
})
