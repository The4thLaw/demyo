/*
 * This plugin filters the data from the 'world-countries' module to only keep what
 * we care about (code, name translations and flag).
 */

import * as fs from 'fs'
import type { Plugin, TransformResult } from 'vite'
import type { Country } from 'world-countries/index.d.ts'

// Not sure there is a way to share this with the frontend...
interface LightCountry {
	cca3: string
	translations: Record<string, { common: string }>
	flag: string
}

export default function worldCountriesFilter(): Plugin<void> {
	return {
		name: 'world-countries-filter',

		transform(src: string, id: string): TransformResult | undefined {
			if (/.*world-countries\/countries\.json$/.test(id)) {
				// Read from the filesystem to get the original file instead of what is already
				// transformed to a JSON module (see console.log(src.substring(0, 100)))
				// This might not be the best way to work but I don't know of a better one right now
				const fileData = fs.readFileSync(id, 'utf8')

				// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
				const allCountries: Country[] = JSON.parse(fileData)

				// Keep a whitelist of interesting things rather than a blacklist, it will
				// ease long-term maintenance
				const modCountries: LightCountry[] = allCountries.map(c => ({
					cca3: c.cca3,
					translations: {
						eng: {
							common: c.name.common
						},
						fra: {
							common: c.translations.fra.common
						}
					},
					flag: c.flag
				} satisfies LightCountry))

				return {
					code: 'export default ' + JSON.stringify(modCountries),
					map: null
				}
			}

			return undefined
		}
	}
}
