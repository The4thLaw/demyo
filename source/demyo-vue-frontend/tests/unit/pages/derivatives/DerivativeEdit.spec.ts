/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-explicit-any */
import DerivativeEdit from '@/pages/derivatives/DerivativeEdit.vue'
import type { VueWrapper } from '@vue/test-utils'
import { mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ref } from 'vue'

vi.mock('vue-router', () => ({
	useRoute: (): any => ({})
}))

vi.mock('@/composables/model-edit', () => ({
	useSimpleEdit: (): any => ({
		model: ref({
			id: undefined,
			series: {},
			album: {},
			artist: {},
			source: {},
			type: {},
			number: 42,
			total: 1337,
			signed: true
		}),
		save: vi.fn(),
		reset: vi.fn(),
		loadData: vi.fn(),
		formRef: ref(null)
	})
}))

vi.mock('@/components/RichTextEditor.vue', () => ({
	default: {
		render: (): any => '[RichTextEditorStub]',
		inheritAttrs: false
	}
}))

describe('DerivativeEdit.vue', () => {
	let wrapper: VueWrapper<unknown, any>

	beforeEach(() => {
		vi.resetAllMocks()
		wrapper = mount(DerivativeEdit)
	})

	it('Load the data', () => {
		const html = wrapper.html()
		expect(html).toMatch('value="42"')
		expect(html).toMatch('value="1337"')
	})

	it('Save the form', async () => {
		const saveBtn = wrapper.findComponent('.c-FormActions__submit')
		await saveBtn.trigger('click')
		expect(wrapper.vm.save).toBeCalled()
	})

	it('Reset the form', async () => {
		const resetBtn = wrapper.findComponent('.c-FormActions__reset')
		await resetBtn.trigger('click')
		expect(wrapper.vm.reset).toBeCalled()
	})
})
