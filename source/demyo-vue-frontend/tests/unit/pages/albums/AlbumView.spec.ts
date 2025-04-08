/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-explicit-any */
import AlbumView from '@/pages/albums/AlbumView.vue'
import type { VueWrapper } from '@vue/test-utils'
import { shallowMount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ref } from 'vue'

vi.mock('vue-router', () => ({
	useRoute: (): any => ({
		params: {
			id: 42
		}
	}),
	useRouter: vi.fn(() => ({
		push: vi.fn()
	}))
}))

// eslint-disable-next-line @typescript-eslint/no-require-imports, @typescript-eslint/no-unsafe-assignment
global.ResizeObserver = require('resize-observer-polyfill')

vi.mock('@/composables/model-view', () => ({
	useSimpleView: (): any => ({
		model: ref({
			id: undefined,
			series: {},
			publisher: {},
			binding: {},
			collection: {}
		}),
		appTaskMenu: ref(false),
		loading: ref(false),
		deleteModel: vi.fn(),
		loadData: vi.fn()
	})
}))

describe('AlbumView.vue', () => {
	let wrapper: VueWrapper<unknown, any>

	beforeEach(() => {
		vi.resetAllMocks()
		wrapper = shallowMount(AlbumView)
	})

	it('Check if there are authors', () => {
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
