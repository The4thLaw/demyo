import countries from 'world-countries/countries.json'
import { useIso639alpha3 } from './i18n'

interface LightCountry {
	cca3: string
	translations: Record<string, { common: string }>
	flag: string
}

const typedCountries: LightCountry[] = countries
const indexedCountries: Record<string, LightCountry> = {}
for (const c of typedCountries) {
	indexedCountries[c.cca3] = c
}

export function useCountryList(): Ref<{ id: string, identifyingName: string }[]> {
	const locale = useIso639alpha3()
	return computed(() => {
		return typedCountries.map(c => ({
			id: c.cca3,
			identifyingName: `${c.translations[locale.value].common} ${c.flag}`
		}))
	})
}

export function useCountry(countryCode: Ref<string>): Ref<string | undefined> {
	const locale = useIso639alpha3()
	return computed(() => {
		if (!countryCode.value) {
			return undefined
		}
		const c = indexedCountries[countryCode.value]
		return `${c.flag} ${c.translations[locale.value].common}`
	})
}

export function useCountries(countryCodes: Ref<string[]>): Ref<string> {
	const locale = useIso639alpha3()
	return computed(() => {
		const l = locale.value
		// Distinct
		const codeSet = new Set(countryCodes.value)
		// Back to array
		const codes = [...codeSet]
		// Sort
		codes.sort((a, b) => {
			const ca = indexedCountries[a].translations[l].common
			const cb = indexedCountries[b].translations[l].common
			return ca.localeCompare(cb)
		})
		// Create the string representation
		return codes.map(code => {
			const c = indexedCountries[code]
			return `${c.flag} ${c.translations[l].common}`
		}).join(', ')
	})
}
