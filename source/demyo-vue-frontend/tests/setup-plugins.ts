import { createTestingPinia } from '@pinia/testing'
import { createHead } from '@unhead/vue/client'
import { config } from '@vue/test-utils'
import { fullscreenImagePlugin } from 'vue-3-fullscreen-image-directive-plugin'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const vuetify = createVuetify({
	components,
	directives
})

config.global.plugins = [
	vuetify,
	fullscreenImagePlugin,
	createTestingPinia({
		stubActions: false
	}),
	createHead()
]
