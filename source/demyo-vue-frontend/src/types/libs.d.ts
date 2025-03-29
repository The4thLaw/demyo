// Not explicitely declared because that's not how it's supposed to be used
declare module 'currency-symbol-map/map' {
	const currencies: Record<string, string>
	export = currencies
}
