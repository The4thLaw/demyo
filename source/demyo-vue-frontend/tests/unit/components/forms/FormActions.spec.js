import FormActions from '@/components/forms/FormActions.vue'
import { mount } from '@vue/test-utils'
import { describe, it } from 'vitest'

global.ResizeObserver = require('resize-observer-polyfill')

function createWrapper(props = {}) {
	return mount(FormActions, {
		props,
		global: {
			components: {
				FormActions
			},
			mocks: {
				$t: s => s
			}
		}
	})
}

describe('FormActions.vue', () => {
	it('Show all buttons by default', () => {
		const wrapper = createWrapper()
		const buttons = wrapper.findAll('.v-btn')
		expect(buttons.at(0).text()).toMatch('button.save')
		expect(buttons.at(1).text()).toMatch('button.reset')
		expect(buttons.at(2).text()).toMatch('button.back')
	})

	it('Can disable buttons', () => {
		const wrapper = createWrapper({
			showReset: false,
			showBack: false
		})
		const buttons = wrapper.findAll('.v-btn')
		expect(buttons.length).toBe(1)
		expect(buttons.at(0).text()).toMatch('button.save')
	})

	it('Should emit events', () => {
		const wrapper = createWrapper()
		const buttons = wrapper.findAll('.v-btn')
		buttons.at(0).trigger('click')
		expect(wrapper.emitted()).toHaveProperty('save')
		buttons.at(1).trigger('click')
		expect(wrapper.emitted()).toHaveProperty('reset')
	})
})
