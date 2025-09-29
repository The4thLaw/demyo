/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-explicit-any */
import TypeIndex from '@/pages/types/TypeIndex.vue'
import { useReaderStore } from '@/stores/reader'
import type { VueWrapper } from '@vue/test-utils'
import { mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'

const readerStore = useReaderStore()
readerStore.currentReader = ({
	configuration: {
		pageSizeForText: 10
	} as ApplicationConfiguration
} as unknown as Reader)

vi.mock('@/services/binding-service')
vi.mock('@/services/derivative-type-service')
vi.mock('@/services/book-type-service')

describe('TypeIndex.vue', () => {
	let wrapper: VueWrapper<unknown, any>

	beforeEach(() => {
		vi.resetAllMocks()
		wrapper = mount(TypeIndex)
	})

	it('Check that type management starts disabled and can be enabled', async () => {
		expect(wrapper.html()).toContain('page.BookType.management.disabled')
		expect(wrapper.html()).toContain('page.BookType.management.enable')
		wrapper.vm.bookTypeManagement = true
		await nextTick()
		expect(wrapper.html()).not.toContain('page.BookType.management.disabled')
	})
})
