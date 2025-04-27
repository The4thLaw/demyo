import '@mdi/font/css/materialdesignicons.css'
import { createHead } from '@unhead/vue/client'
import 'roboto-fontface/css/roboto/roboto-fontface.css'
import type { Component, ComponentOptions } from 'vue'
// From https://gitlab.com/totol.toolsuite/vue-3-fullscreen-image
import { fullscreenImagePlugin } from 'vue-3-fullscreen-image-directive-plugin'
import 'vue-3-fullscreen-image-directive-plugin/style.css'
import App from './App.vue'
import i18n from './i18n'
import vuetify from './plugins/vuetify'
import router from './router'
import readerService from './services/reader-service'
import pinia from './stores'

const app = createApp(App as Component)

app.use(pinia)
// Do these as soon as possible but after pinia is set up. It's asynchronous and will work during bootstrap
void readerService.init()

// Global mixin to allow components to scroll to the top of the page
app.mixin({
	methods: {
		scrollToTop: () => window.scroll(0, 0)
	}
} as ComponentOptions)

app.use(router)
app.use(i18n)
app.use(vuetify)
app.use(fullscreenImagePlugin)

app.use(createHead())

// Add the ID to the body element to ease style overrides
document.getElementsByTagName('body')[0].id = 'demyo'

app.mount('#app')
