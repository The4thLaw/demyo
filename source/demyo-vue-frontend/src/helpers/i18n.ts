import i18n from '@/i18n'
import getSymbolFromCurrency from 'currency-symbol-map'
import rawCurrencyMap from 'currency-symbol-map/map'

// Process the currency map to show displayable values
const processedCurrencyList = []
for (const key in rawCurrencyMap) {
	let val = rawCurrencyMap[key]
	if (val !== key) {
		val = `${key} (${val})`
	}

	processedCurrencyList.push({
		value: key,
		title: val
	})
}
export const currencyList = processedCurrencyList

/**
 * Checks if the selected locale shows currencies as a prefix of the amounts. en does, fr doesn't, for example.
 * @returns true if the selected locale shows currencies as prefixes.
 */
export function isCurrencyPrefix(): boolean {
	// TODO: ideally, this NumberFormat should be created once, at the change of locale
	const locale = i18n.global.locale.value.replace(/_/g, '-')
	return new Intl.NumberFormat(locale, { style: 'currency', currency: 'XTS' }).format(1).startsWith('XTS')
}

/**
 * Formats an amount with a currency, caring for unknown or invalid currencies.
 *
 * This function is not perfect because invalid currencies will use the number of decimals from US$.
 * But there's not much we can do if the provided currency is not a currency code.
 *
 * @param amount The amount to format
 * @param currency The currency to use
 * @returns The formatted currency amount.
 */
export function formatCurrency(amount?: number, currency?: string): string | undefined {
	if (amount === undefined) {
		return undefined
	}
	if (!currency) {
		return i18n.global.n(amount)
	}

	// The browser could already understand the code but the map is more complete, and gives
	// lookup capabilities that we don't have in the browser
	currency = getCurrencySymbol(currency)

	// Format with a default currency
	const formatted = new Intl.NumberFormat(i18n.global.locale.value,
		{ style: 'currency', currency: 'XTS' }).format(amount)

	// Pad the currency if needed. Worst case: it's padded twice and HTML strips the extra padding
	if (currency.length !== 1 && formatted.startsWith('XTS')) {
		// Pad the currency
		currency += ' '
	}

	// Adjust the currency
	return formatted.replace('XTS', currency)
}

/**
 * Finds the currency symbol based on its currency code
 * @param currency The currency symbol or code
 * @returns The currency symbol
 */
export function getCurrencySymbol(currency: string): string {
	if (!currency || currency.length !== 3) {
		return currency
	}

	// Looks like a currency code, try the map
	return getSymbolFromCurrency(currency) ?? currency
}

export function useIso639alpha3(): Ref<string> {
	return computed(() => {
		if (i18n.global.locale.value.startsWith('fr')) {
			return 'fra'
		}
		return 'eng'
	})
}
