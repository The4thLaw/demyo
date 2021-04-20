import getSymbolFromCurrency from 'currency-symbol-map'
import rawCurrencyMap from 'currency-symbol-map/map'
import i18n from '@/i18n'

// Process the currency map to show displayable values
const processedCurrencyList = []
for (const key in rawCurrencyMap) {
	let val = rawCurrencyMap[key]
	if (val !== key) {
		val = `${key} (${val})`
	}

	processedCurrencyList.push({
		value: key,
		text: val
	})
}
export const currencyList = processedCurrencyList

/**
 * Checks if the selected locale shows currencies as a prefix of the amounts. en does, fr doesn't, for example.
 * @returns true if the selected locale shows currencies as prefixes.
 */
export function isCurrencyPrefix() {
	// TODO: ideally, this NumberFormat should be created once, at the change of locale
	const locale = i18n.locale.replace(/_/g, '-')
	return new Intl.NumberFormat(locale, { style: 'currency', currency: 'XTS' }).format(1).startsWith('XTS')
}

/**
 * Formats an amount with a currency, caring for unknown or invalid currencies.
 *
 * This function is not perfect because invalid currencies will use the number of decimals from US$.
 * But there's not much we can do if the provided currency is not a currency code.
 *
 * @param {Number} amount The amount to format
 * @param {String} currency The currency to use
 * @returns The formatted currency amount.
 */
// TODO: use https://vuetifyjs.com/en/components/combobox/ to provide the currencies.
// This lets users select €, $, Yen with the right currency codes. E.G. { text: 'EUR (€)', value: 'EUR' }
// Put the known currency codes in a JSON file
export function formatCurrency(amount, currency) {
	if (!currency) {
		return i18n.n(amount)
	}

	// The browser could already understand the code but the map is more complete, and gives
	// lookup capabilities that we don't have in the browser
	currency = getCurrencySymbol(currency)

	// Format with a default currency
	const formatted = new Intl.NumberFormat(i18n.locale, { style: 'currency', currency: 'XTS' }).format(amount)

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
 * @param {String} currency The currency symbol or code
 * @returns The currency symbol
 */
export function getCurrencySymbol(currency) {
	if (!currency || currency.length !== 3) {
		return currency
	}

	// Looks like a currency code, try the map
	return getSymbolFromCurrency(currency) || currency
}
