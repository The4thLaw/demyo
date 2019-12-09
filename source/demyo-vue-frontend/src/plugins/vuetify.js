import Vue from 'vue'
import Vuetify from 'vuetify/lib'
import { Touch } from 'vuetify/lib/directives'
import colors from 'vuetify/lib/util/colors'
import { TiptapVuetifyPlugin } from 'tiptap-vuetify'
import 'tiptap-vuetify/dist/main.css'

Vue.use(Vuetify, {
	directives: {
		Touch
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

Vue.use(TiptapVuetifyPlugin, {
	// the next line is important! You need to provide the Vuetify Object to this place.
	vuetify, // same as "vuetify: vuetify"
	// optional, default to 'md' (default vuetify icons before v2.0.0)
	iconsGroup: 'mdi'
})

export default vuetify
