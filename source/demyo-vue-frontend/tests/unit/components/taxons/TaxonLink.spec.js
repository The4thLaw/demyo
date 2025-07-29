import TaxonLink from '@/components/tags/TaxonLink.vue'
import { mount, RouterLinkStub } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'

describe('TaxonLink.vue', () => {
	/** @type VueWrapper */
	let wrapper

	beforeEach(() => {
		// Don't do a shallow mount, we need the callback from ModelLink with scoped slots
		wrapper = mount(TaxonLink, {
			global: {
				stubs: {
					RouterLink: RouterLinkStub
				}
			},
			props: {
				model: {
					id: '42',
					identifyingName: 'Sample tag',
					usageCount: 1337
				}
			}
		})
	})

	it('Render a single tag', () => {
		expect(wrapper.text()).toMatch('Sample tag')
		expect(wrapper.find('.d-Taxon__count').text()).toBe('1337')
	})

	it('Produce the proper style', () => {
		const tagData = {
			fgColour: '#fabfab',
			bgColour: '#b0fb0f',
			relativeWeight: '120'
		}
		const style = wrapper.vm.getStyle(tagData)
		expect(style).toMatchObject({
			color: tagData.fgColour,
			backgroundColor: tagData.bgColour,
			fontSize: tagData.relativeWeight + '%'
		})
	})

	it('Check the usage correctly', () => {
		expect(wrapper.vm.hasCount({})).toBeFalsy()
		expect(wrapper.vm.hasCount({ usageCount: 0 })).toBeTruthy()
		expect(wrapper.vm.hasCount({ usageCount: 1 })).toBeTruthy()
	})
})
