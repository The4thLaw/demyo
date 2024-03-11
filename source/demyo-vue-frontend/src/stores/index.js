import { createPinia, PiniaVuePlugin } from 'pinia'
import Vue from 'vue'

Vue.use(PiniaVuePlugin)
const pinia = createPinia()
Vue.use(pinia)

export default pinia
