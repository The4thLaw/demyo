/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-explicit-any */
import AlbumCard from '@/components/AlbumCard.vue'
import type { VueWrapper } from '@vue/test-utils'
import { mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'

describe('AlbumCard.vue', () => {
	let wrapper: VueWrapper<unknown, any>

	beforeEach(() => {
		vi.resetAllMocks()
		wrapper = mount(AlbumCard, {
			props: {
				album: {
					id: 13,
					identifyingName: 'My album title',
					currentEditionDate: new Date('2024-01-01'),
					cover: {
						id: 42,
						identifyingName: 'My cover',
						url: 'my/url',
						userFileName: 'My user file name'
					}
				} satisfies Partial<Album>
			}
		})
	})

	it('Defaults to not expanded', () => {
		expect(wrapper.vm.expanded).toBeFalsy()
		console.log()
	})

	it('Displays album information', () => {
		expect(wrapper.findComponent('.c-AlbumCard__albumLink').text()).toContain('My album title')
		expect(wrapper.findComponent('.v-card-text .c-FieldValue').text()).toContain('Jan 01 2024')
	})
})
