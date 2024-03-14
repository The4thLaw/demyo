//import { TiptapVuetifyPlugin } from 'tiptap-vuetify'
//import 'tiptap-vuetify/dist/main.css'
import { createVuetify } from 'vuetify'
import 'vuetify/styles'
// TODO: Vue 3: maybe we can remove the separate mdi import then?
import { aliases, mdi } from 'vuetify/iconsets/mdi'

/*Vue.use(Vuetify, {
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
})*/

const vuetify = createVuetify({
	icons: {
		defaultSet: 'mdi',
		aliases,
		sets: {
			mdi
		}
	}
	// TODO: Vue 3
	/*theme: {
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
	}*/
})

//Vue.use(vuetify)

/*Vue.use(TiptapVuetifyPlugin, {
	vuetify,
	iconsGroup: 'mdi'
})*/

export default vuetify
