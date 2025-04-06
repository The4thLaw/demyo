import { formatCurrency } from '@/helpers/i18n'
import { useReaderStore } from '@/stores/reader'
import { computed } from 'vue'

export function useCurrency(rawPrice: Ref<number>)
		: { currency: Ref<string>, qualifiedPrice: Ref<string | undefined> } {
	const asArray = computed(() => [rawPrice.value])
	const currencies = useCurrencies(asArray)
	return {
		currency: currencies.currency,
		qualifiedPrice: computed(() => currencies.qualifiedPrices.value[0])
	}
}

export function useCurrencies(rawPrices: Ref<number[]>)
		: { currency: Ref<string>, qualifiedPrices: Ref<(string | undefined)[]> } {
	const readerStore = useReaderStore()

	const currency = computed(() => readerStore.currentReader.configuration.currency)
	const qualifiedPrices = computed(() => rawPrices.value.map(p => formatCurrency(p, currency.value)))

	return {
		currency,
		qualifiedPrices
	}
}
