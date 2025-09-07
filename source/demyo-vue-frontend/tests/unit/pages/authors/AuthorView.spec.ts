/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-explicit-any */
import AuthorView from '@/pages/authors/AuthorView.vue'
import type { VueWrapper } from '@vue/test-utils'
import { shallowMount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ref } from 'vue'

vi.mock('@/composables/model-view', () => ({
	useSimpleView: (): any => ({
		model: ref({
			id: undefined,
			portrait: {}
		}),
		appTaskMenu: ref(false),
		loading: ref(false),
		deleteModel: vi.fn(),
		loadData: vi.fn()
	})
}))

describe('AuthorView.vue', () => {
	let wrapper: VueWrapper<unknown, any>

	beforeEach(() => {
		vi.resetAllMocks()
		wrapper = shallowMount(AuthorView)
	})

	it('Check if the birthday is computed correctly', () => {
		expect(wrapper.vm.isBirthday).toBeFalsy()

		const today = new Date()
		wrapper.vm.author.birthDate = today
		expect(wrapper.vm.isBirthday).toBeTruthy()

		const yesterday = new Date()
		yesterday.setDate(today.getDate() - 1)
		wrapper.vm.author.birthDate = yesterday
		expect(wrapper.vm.isBirthday).toBeFalsy()
	})
})
