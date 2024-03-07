import Vue from 'vue'
import Vuex from 'vuex'
import ReaderModule from './reader'

Vue.use(Vuex)

export default new Vuex.Store({
	modules: {
		reader: ReaderModule
	}
})
