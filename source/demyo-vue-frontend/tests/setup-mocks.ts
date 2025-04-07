import { createTestingPinia } from '@pinia/testing'
import { config, RouterLinkStub } from '@vue/test-utils'
import { vi } from 'vitest'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

config.global.mocks = {
	$t: (s: string): string => s,
	$d: (s: string): string => s
}

config.global.stubs = {
	// eslint-disable-next-line @typescript-eslint/naming-convention
	RouterLink: RouterLinkStub
}

const vuetify = createVuetify({
	components,
	directives
})
config.global.plugins = [vuetify,
	createTestingPinia({
		stubActions: false
	})
]

vi.mock('@/i18n', () => ({
	loadLocaleMessages: (): unknown => ({})
}))

vi.mock('vue-i18n', () => ({
	// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
	useI18n: () => ({
		t: (key: string): string => key,
		d: (key: string): string => key
	})
}))

vi.resetModules()
