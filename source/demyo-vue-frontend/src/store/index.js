import Vue from 'vue'
import Vuex from 'vuex'
import UiModule from './ui'

Vue.use(Vuex)

export default new Vuex.Store({
	modules: {
		ui: UiModule
	}
})
