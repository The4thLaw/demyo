import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import i18n from './i18n'
import readerService from '@/services/reader-service'
import './plugins/portal.js'
import './plugins/vimg.js'
import vuetify from './plugins/vuetify'
import 'roboto-fontface/css/roboto/roboto-fontface.css'
import '@mdi/font/css/materialdesignicons.css'

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
	store,
	i18n,
	vuetify,
	render: h => h(App)
}).$mount('#app')
