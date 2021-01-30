import Vue from 'vue'
import Vuetify from 'vuetify'
import { config } from '@vue/test-utils'

Vue.use(Vuetify)

// Global mock for Vue-i18n
config.mocks.$t = (a) => a
