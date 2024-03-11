import { formatCurrency } from '@/helpers/i18n'
import { useReaderStore } from '@/stores/reader'
import { mapState } from 'pinia'

export default {
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
