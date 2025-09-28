import axios from 'axios'
import type { Entities, EntityId, parse, SimplifiedEntity, SimplifiedItem, SimplifiedLabels } from 'wikibase-sdk'
import wdk from 'wikibase-sdk/wikidata.org'

const SEARCH_LIMIT = 10
const FALLBACK_LANG = 'en'

function instanceOfSimplifiedItem(object: SimplifiedEntity): object is SimplifiedItem {
	return 'type' in object && object.type === 'item'
}

export interface PeopleSearchResult {
	id: string
	fullName: string
	description: string
	item: SimplifiedItem
}

export async function searchPeople(term: string, language: string): Promise<PeopleSearchResult[]> {
	console.debug(`Searching for '${term}' in '${language}'`)

	// There are to approaches here. We could search for all entities and get a description, but then we
	// need to fetch all entities to check if they are human. Or we can search for humans and get less
	// base information and fetch everything afterwards
	// In order to limit the calls to get full entities I went for the second, called cirrusSearch
	// but the first one implied a call to wdk.searchEntities

	const cirrusSearchUrl = wdk.cirrusSearchPages({
		search: term,
		haswbstatement: 'P31=Q5', // Humans
		limit: SEARCH_LIMIT
	})
	const cirrusSearchResponse = (await axios.get<parse.CirrusSearchPagesResponse>(cirrusSearchUrl)).data
	const ids = wdk.parse.pagesTitles(cirrusSearchResponse)

	if (!ids.length) {
		return []
	}

	const languages = language === FALLBACK_LANG ? [FALLBACK_LANG] : [language, FALLBACK_LANG]

	const entitiesQuery = wdk.getEntities({
		ids: ids as EntityId[],
		languages
	})
	const entities: Entities = (await axios.get<parse.WbGetEntitiesResponse>(entitiesQuery)).data.entities

	const simplified = wdk.simplify.entities(entities)

	const items = Object.values(simplified)
		.filter(e => instanceOfSimplifiedItem(e))
		// Only keep results with understandable labels
		.filter(e => withFallback(e.labels, language) !== undefined)
		.map(e => ({
			id: e.id as string,
			fullName: withFallback(e.labels, language) ?? e.id, // Coalescing should never happen
			description: withFallback(e.descriptions, language) ?? '',
			item: e
		}))

	console.log('People search results: ', items)

	return items
}
function withFallback(labels: SimplifiedLabels | undefined, language: string): string | undefined {
	return labels ? labels[language] ?? labels[FALLBACK_LANG] : undefined
}
