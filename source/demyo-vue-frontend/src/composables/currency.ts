import { formatCurrency } from '@/helpers/i18n'
import { useReaderStore } from '@/stores/reader'
import { computed } from 'vue'

export function useCurrency(rawPrice: number) {
	const currencies = useCurrencies([rawPrice])
	return {
		currency: currencies.currency,
		qualifiedPrice: ref(currencies.qualifiedPrices.value[0])
	}
}

export function useCurrencies(rawPrices: number[]) {
	const readerStore = useReaderStore()

	const currency = computed(() => readerStore.currentReader?.configuration?.currency)
	const qualifiedPrices = computed(() => rawPrices.map(p => formatCurrency(p, currency.value)))

	return {
		currency,
		qualifiedPrices
	}
}
