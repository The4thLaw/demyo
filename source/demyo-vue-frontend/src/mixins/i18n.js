import { formatCurrency } from '@/helpers/i18n'
import { useReaderStore } from '@/stores/reader'
import { mapState } from 'pinia'

export default {
	// TODO: Vue 3: remove this, replace with a composable or a direct call
	filters: {
		price(amount, currency) {
			return formatCurrency(amount, currency)
		}
	},

	computed: {
		...mapState(useReaderStore, {
			currency: function (store) {
				return store.currentReader?.configuration?.currency
			}
		})
	}
}
