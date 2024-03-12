import { cspStyleNonce } from '@/myenv'
import { TiptapVuetifyPlugin } from 'tiptap-vuetify'
import 'tiptap-vuetify/dist/main.css'
import Vue from 'vue'
import Vuetify from 'vuetify/lib'
import { Ripple, Touch } from 'vuetify/lib/directives'
import colors from 'vuetify/lib/util/colors'

Vue.use(Vuetify, {
	directives: {
		Touch,
		// TODO: Workaround for https://github.com/vuetifyjs/vuetify/issues/12224, to be removed in Vue+Vuetify 3
		Ripple
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
