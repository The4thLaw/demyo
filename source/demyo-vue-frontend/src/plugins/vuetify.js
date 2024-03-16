import { cspStyleNonce } from '@/myenv'
// import { TiptapVuetifyPlugin } from 'tiptap-vuetify'
// import 'tiptap-vuetify/dist/main.css'
import { createVuetify } from 'vuetify'
import 'vuetify/styles'
// TODO: Vue 3: maybe we can remove the separate mdi import then?
import { aliases, mdi } from 'vuetify/iconsets/mdi'

const vuetify = createVuetify({
	icons: {
		defaultSet: 'mdi',
		aliases,
		sets: {
			mdi
		}
	},
	theme: {
		cspNonce: cspStyleNonce
	}

	// TODO: Vue 3
	/*
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
	*/
})

export default vuetify
