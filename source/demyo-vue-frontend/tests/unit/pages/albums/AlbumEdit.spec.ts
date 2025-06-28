/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-explicit-any */
import AlbumEdit from '@/pages/albums/AlbumEdit.vue'
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
			publisher: {},
			binding: {},
			collection: {},
			cover: {},
			firstEditionDate: new Date('2024-01-01')
		}),
		save: vi.fn(),
		reset: vi.fn(),
		loadData: vi.fn(),
		formRef: ref(null)
	})
}))

vi.mock('@/components/forms/RichTextEditor.vue', () => ({
	default: {
		render: (): any => '[RichTextEditorStub]',
		inheritAttrs: false
	}
}))

describe('AlbumEdit.vue', () => {
	let wrapper: VueWrapper<unknown, any>

	beforeEach(() => {
		vi.resetAllMocks()

		wrapper = mount(AlbumEdit)
	})

	it('Load the data', () => {
		const html = wrapper.html()
		expect(html).toMatch(/<input [^>]* value="[^"]*Jan 01 2024/)
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
