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

// TODO: instead of doing this, set a fake reader in the store to bootstrap the application faster
// This fake reader could have a specific fake flag to recognize it and avoid getting favourite...
// Or maybe a specific "isLoaded" state
// init() would then be called from the reader service constructor and be ready when it's ready

readerService.init().then(() => {
	console.log('Reader is initialized, bootstrapping the application...')

	new Vue({
		router,
		store,
		i18n,
		vuetify,
		render: h => h(App)
	}).$mount('#app')
})
