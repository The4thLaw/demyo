import Vue from 'vue'
import Vuetify from 'vuetify/lib'
import { Touch } from 'vuetify/lib/directives'
import colors from 'vuetify/lib/util/colors'

Vue.use(Vuetify, {
	directives: {
		Touch
	}
})

console.log(Touch)

export default new Vuetify({
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
				anchor: colors.pink,
				accent: colors.pink
			}
		}
	}
})
