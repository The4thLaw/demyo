import Vue from 'vue'
import Vuex from 'vuex'
import ReaderModule from './reader'
import UiModule from './ui'

Vue.use(Vuex)

export default new Vuex.Store({
	modules: {
		reader: ReaderModule,
		ui: UiModule
	}
})
