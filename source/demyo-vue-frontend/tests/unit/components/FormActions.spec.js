import FormActions from '@/components/FormActions.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vuetify from 'vuetify'

const localVue = createLocalVue()

function checkEvent(wrapper, button, eventName) {
	const event = jest.fn()
	wrapper.vm.$on(eventName, event)
	expect(event).toHaveBeenCalledTimes(0)
	button.trigger('click')
	expect(event).toHaveBeenCalledTimes(1)
}

describe('FormActions.vue', () => {
	let vuetify

	beforeEach(() => {
		vuetify = new Vuetify()
	})

	it('Shows all buttons by default', () => {
		const wrapper = mount(FormActions, {
			localVue,
			vuetify
		})

		const buttons = wrapper.findAll('.v-btn')
		expect(buttons.at(0).text()).toMatch('button.save')
		expect(buttons.at(1).text()).toMatch('button.reset')
		expect(buttons.at(2).text()).toMatch('button.back')
	})

	it('Shows all buttons by default', () => {
		const wrapper = mount(FormActions, {
			localVue,
			vuetify
		})

		const buttons = wrapper.findAll('.v-btn')
		expect(buttons.at(0).text()).toMatch('button.save')
		expect(buttons.at(1).text()).toMatch('button.reset')
		expect(buttons.at(2).text()).toMatch('button.back')
	})

	it('Can disable buttons', () => {
		const wrapper = mount(FormActions, {
			localVue,
			vuetify,
			propsData: {
				showReset: false,
				showBack: false
			}
		})

		const buttons = wrapper.findAll('.v-btn')
		expect(buttons.length).toBe(1)
		expect(buttons.at(0).text()).toMatch('button.save')
	})

	it('Should emit events', () => {
		const wrapper = mount(FormActions, {
			localVue,
			vuetify,
			propsData: {
				showBack: false
			}
		})

		const buttons = wrapper.findAll('.v-btn')

		checkEvent(wrapper, buttons.at(0), 'save')
		checkEvent(wrapper, buttons.at(1), 'reset')
	})
})
