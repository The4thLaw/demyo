import '@mdi/font/css/materialdesignicons.css'
import 'roboto-fontface/css/roboto/roboto-fontface.css'
import Vue from 'vue'
import App from './App.vue'
import i18n from './i18n'
import './plugins/portal.js'
import './plugins/vimg.js'
import vuetify from './plugins/vuetify'
import router from './router'
import readerService from './services/reader-service'
import pinia from './stores'
import './webpack-public-path'

Vue.config.productionTip = false

// Do this as soon as possible. It's asynchronous and will work during bootstrap
readerService.init()

// Global mixin to allow components to scroll to the top of the page
Vue.mixin({
	methods: {
		scrollToTop: () => window.scroll(0, 0)
	}
})

new Vue({
	router,
	pinia,
	i18n,
	vuetify,
	render: h => h(App)
}).$mount('#app')
