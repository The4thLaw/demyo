import { mount } from '@vue/test-utils'
import TagLink from '@/components/TagLink.vue'

describe('TagLink.vue', () => {
	let wrapper

	beforeEach(() => {
		// Don't do a shallow mount, we need the callback from ModelLink with scoped slots
		wrapper = mount(TagLink, {
			propsData: {
				model: {
					id: '42',
					identifyingName: 'Sample tag',
					usageCount: 1337
				}
			},
			stubs: ['router-link', 'router-view']
		})
	})

	it('Renders a single tag', () => {
		expect(wrapper.text()).toMatch('Sample tag')
		expect(wrapper.find('.d-Tag__count').text()).toBe('1337')
	})

	it('Produces the proper style', () => {
		const tagData = {
			fgColour: '#fabfab',
			bgColour: '#b0fb0f',
			relativeWeight: '120'
		}
		const style = wrapper.vm.getStyle(tagData)
		expect(style).toMatchObject({
			color: tagData.fgColour,
			'background-color': tagData.bgColour,
			'font-size': tagData.relativeWeight + '%'
		})
	})

	it('Checks the usage correctly', () => {
		expect(wrapper.vm.hasCount({})).toBeFalsy()
		expect(wrapper.vm.hasCount({ usageCount: 0 })).toBeTruthy()
		expect(wrapper.vm.hasCount({ usageCount: 1 })).toBeTruthy()
	})
})
