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

/*
TODO: instead of doing this, set a fake reader in the store to bootstrap the application faster
This fake reader could have a specific fake flag to recognize it and avoid getting favourites...
Or maybe a specific "isLoaded" state. Not sure if it's really needed
init() would then be called from the reader service constructor and be ready when it's ready
Once ready, it would set the reader in the store
If setting a reader in the store is not possible, it would set a specific requiresReaderSelection in the store
that would be watched by App. In such cases, App would show a *modal* dialog apologizing for the interruption
and requesting that the user select his reader. This dialog could also be used to switch readers in the
regular process, if the dialog title can be configured. In "select" mode, it should still be possible to
delete readers, as maybe it's at that time that the user will realise that he shouldn't have a specific reader.
Same for adding readers
This would also remove the need for specific guards in the router since the App would automatically react
to missing data

Checking the favourites of other readers could be done directly from the favourites pages, rather than from
the selection dialog as it was done in
*/

// Do this as soon as possible. It's asynchronous and will work during bootstrap
readerService.init()

new Vue({
	router,
	store,
	i18n,
	vuetify,
	render: h => h(App)
}).$mount('#app')
