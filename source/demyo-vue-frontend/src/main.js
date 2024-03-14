import '@mdi/font/css/materialdesignicons.css'
import portalVue from 'portal-vue'
import 'roboto-fontface/css/roboto/roboto-fontface.css'
import { createApp } from 'vue'
import App from './App.vue'
import i18n from './i18n'
import './plugins/vimg.js'
import vuetify from './plugins/vuetify'
import router from './router'
import readerService from './services/reader-service'
import pinia from './stores'

// Do this as soon as possible. It's asynchronous and will work during bootstrap
readerService.init()

const app = createApp(App)

// Global mixin to allow components to scroll to the top of the page
app.mixin({
	methods: {
		scrollToTop: () => window.scroll(0, 0)
	}
})

app.use(router)
app.use(i18n)
app.use(pinia)
app.use(portalVue)
app.use(vuetify)

app.mount('#app')
