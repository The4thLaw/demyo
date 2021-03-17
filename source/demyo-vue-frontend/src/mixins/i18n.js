import { mapState } from 'vuex'
import { formatCurrency } from '@/helpers/i18n'

export default {
	filters: {
		price(amount, currency) {
			return formatCurrency(amount, currency)
		}
	},

	computed: {
		...mapState({
			currency: function (state) {
				return state.reader.currentReader?.configuration?.currency
			}
		})
	}
}
