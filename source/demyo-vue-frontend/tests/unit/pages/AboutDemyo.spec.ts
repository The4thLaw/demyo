/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-explicit-any */
import AboutDemyo from '@/pages/AboutDemyo.vue'
import { mount, type VueWrapper } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'

vi.mock('@/services/about-service', () => ({
	default: {
		getEnvironment: (): Record<string, string> => {
			return {
				osName: 'Test OS',
				osVersion: '42',
				osArch: 'x64',
				javaVendor: 'Adoptium',
				javaVersion: '17'
			}
		}
	}
}))

describe('AlbumView.vue', () => {
	let wrapper: VueWrapper<unknown, any>

	beforeEach(() => {
		vi.resetAllMocks()
		wrapper = mount(AboutDemyo)
	})

	it('Show the system parameters', () => {
		expect(wrapper.vm.env.osVersion).toEqual('42')
		const table = wrapper.findComponent('#v-About__aboutInstall')
		const text = table.text()
		expect(text).toContain('Adoptium Java (17)')
	})
})
