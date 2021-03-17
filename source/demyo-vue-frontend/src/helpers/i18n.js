import i18n from '@/i18n'

/**
 * Checks if the selected locale shows currencies as a prefix of the amounts. en does, fr doesn't, for example.
 * @returns true if the selected locale shows currencies as prefixes.
 */
export function isCurrencyPrefix() {
	return new Intl.NumberFormat(i18n.locale, { style: 'currency', currency: 'XTS' }).format(1).startsWith('XTS')
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

	if (currency.length === 3) {
		// Looks like a currency code, rely on the browser to format it
		try {
			return new Intl.NumberFormat(i18n.locale, { style: 'currency', currency: currency }).format(amount)
		} catch {
			// Invalid currency (note that the browser can cope with 'AAA' but not '€€€')
		}
	}

	// Either the browser doesn't recognize the currency, or it's not a currency code

	// Format with a default currency that is very likely to be known
	const formatted = new Intl.NumberFormat(i18n.locale,
		{ style: 'currency', currency: 'USD', currencyDisplay: 'narrowSymbol' }).format(amount)

	// Pad the currency if needed. Worst case: it's padded twice and HTML strips the extra padding
	if (currency.length !== 1 && formatted.startsWith('$')) {
		// Pad the currency
		currency += ' '
	}
	return formatted.replace('$', currency)
}
