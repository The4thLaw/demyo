import Vue from 'vue'
import Vuetify from 'vuetify/lib'
import { Touch } from 'vuetify/lib/directives'
import colors from 'vuetify/lib/util/colors'
import { TiptapVuetifyPlugin } from 'tiptap-vuetify'
import 'tiptap-vuetify/dist/main.css'
import { cspStyleNonce } from '@/myenv'

Vue.use(Vuetify, {
	directives: {
		Touch
	},
	theme: {
		options: {
			cspNonce: cspStyleNonce
		}
	}
})

const vuetify = new Vuetify({
	icons: {
		iconfont: 'mdi'
	},
	theme: {
		options: {
			customProperties: true
		},

		themes: {
			light: {
				primary: colors.blue,
				anchor: colors.pink.base,
				accent: colors.pink
			}
		}
	}
})

// Note that due to tiptap's tight integration with Vue, it doesn't seem to be possible to
// lazy-load it in a distinct chunk.
// Else we could use the following techniques to load the tipTapExtensions and the component :
// - https://forum.vuejs.org/t/lazy-load-3rd-party-library-like-leaflet/7122/2
// - https://vueschool.io/articles/vuejs-tutorials/lazy-loading-individual-vue-components-and-prefetching/
Vue.use(TiptapVuetifyPlugin, {
	vuetify,
	iconsGroup: 'mdi'
})

export default vuetify
