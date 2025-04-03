/* eslint-disable @typescript-eslint/naming-convention */
import { cspStyleNonce } from '@/myenv'
import { createVuetify } from 'vuetify'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import 'vuetify/styles'
import colors from 'vuetify/util/colors'

const demyoTheme = {
	dark: false,
	colors: {
		background: '#eee',
		primary: colors.blue.base,
		secondary: colors.pink.base
	}
}

const vuetify = createVuetify({
	// Use the MDI icons which we still must include manually as a dependency (so we control the version)
	icons: {
		defaultSet: 'mdi',
		aliases,
		sets: {
			mdi
		}
	},
	// Set the color and CSP nonce
	theme: {
		cspNonce: cspStyleNonce,
		defaultTheme: 'demyoTheme',
		themes: {
			demyoTheme
		}
	},
	// Set some defaults for components to keep the Demyo 2 style that we like
	defaults: {
		VAutocomplete: {
			color: 'primary',
			variant: 'underlined'
		},
		VCheckbox: {
			color: 'primary'
		},
		VFileInput: {
			color: 'primary',
			variant: 'underlined'
		},
		VPagination: {
			variant: 'flat',
			activeColor: 'primary',
			rounded: 'circle',
			totalVisible: 8,
			class: 'my-4'
		},
		VSelect: {
			color: 'primary',
			variant: 'underlined'
		},
		VTextField: {
			color: 'primary',
			variant: 'underlined'
		}
	}
})

export default vuetify
