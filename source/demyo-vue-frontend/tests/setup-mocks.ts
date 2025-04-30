import { config, RouterLinkStub } from '@vue/test-utils'
import { vi } from 'vitest'

config.global.mocks = {
	$t: (s: string): string => s,
	$d: (s: string): string => s
}

config.global.stubs = {
	// eslint-disable-next-line @typescript-eslint/naming-convention
	RouterLink: RouterLinkStub
}

vi.mock('@/i18n', () => ({
	default: {
		global: {
			locale: ref('en')
		}
	},
	loadLocaleMessages: (): unknown => ({}),
	$t: (key: string): string => key
}))

vi.mock('vue-i18n', () => ({
	// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
	useI18n: () => ({
		t: (key: string): string => key,
		d: (key: string): string => key
	})
}))

vi.resetModules()
