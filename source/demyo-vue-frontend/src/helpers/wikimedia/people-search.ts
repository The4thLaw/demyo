import axios from 'axios'
import type {
	Entities, EntityId, parse, PropertyId, SimplifiedClaims,
	SimplifiedEntity, SimplifiedItem, SimplifiedLabels
} from 'wikibase-sdk'
import wdk from 'wikibase-sdk/wikidata.org'

const SEARCH_LIMIT = 10
const FALLBACK_LANG = 'en'

const P_GIVEN_NAME = 'P735'
const P_FAMILY_NAME = 'P734'
const P_NATIVE_NAME = 'P1559'
const P_DOB = 'P569'
const P_DOD = 'P570'
const P_CITIZENSHIP = 'P27'
const P_ISO_3166_ALPHA_3 = 'P298'

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
	console.debug(`Searching for people named '${term}' in '${language}'`)

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

	const languages = getLanguages(language)

	const entitiesQuery = wdk.getEntities({
		ids: ids as EntityId[],
		languages
	})
	const entities: Entities = (await axios.get<parse.WbGetEntitiesResponse>(entitiesQuery)).data.entities

	const simplified = wdk.simplify.entities(entities)

	return Object.values(simplified)
		.filter(e => instanceOfSimplifiedItem(e))
		// Only keep results with understandable labels
		.filter(e => withFallback(e.labels, language) !== undefined)
		.map(e => ({
			id: e.id as string,
			fullName: withFallback(e.labels, language) ?? e.id, // Coalescing should never happen
			description: withFallback(e.descriptions, language) ?? '',
			item: e
		}))
}

function getLanguages(language: string): string[] {
	return language === FALLBACK_LANG ? [FALLBACK_LANG] : [language, FALLBACK_LANG]
}

function withFallback(labels: SimplifiedLabels | undefined, language: string): string | undefined {
	return labels ? labels[language] ?? labels[FALLBACK_LANG] : undefined
}

export async function loadPerson(psr: PeopleSearchResult, language: string): Promise<Partial<Author>> {
	if (!psr.item.claims) {
		console.warn('No claims to extract data from', psr.item)
		return {}
	}

	const indirectIds = [
		...psr.item.claims[P_GIVEN_NAME] as EntityId[],
		...psr.item.claims[P_FAMILY_NAME] as EntityId[],
		...psr.item.claims[P_CITIZENSHIP] as EntityId[]
	]
	const languages = getLanguages(language)
	const indirectQuery = wdk.getEntities({ ids: indirectIds, languages })
	const indirectEntities: Entities = (await axios.get<parse.WbGetEntitiesResponse>(indirectQuery)).data.entities
	const simplified = wdk.simplify.entities(indirectEntities)

	const author: Partial<Author> = {}

	author.nativeLanguageName = (psr.item.claims[P_NATIVE_NAME]?.[0] ?? '') as string
	author.name = simplifyClaims(psr.item.claims, P_FAMILY_NAME, simplified, language)
	author.firstName = simplifyClaims(psr.item.claims, P_GIVEN_NAME, simplified, language)

	const citizenshipId = (psr.item.claims[P_CITIZENSHIP as PropertyId]?.[0] ?? '') as string
	if (citizenshipId) {
		const citizenship = simplified[citizenshipId] as SimplifiedItem
		const countryCode = citizenship.claims ? (citizenship.claims[P_ISO_3166_ALPHA_3]?.[0] ?? '') as string : undefined
		author.country = countryCode ?? ''
	}

	// Strip the time parts of the dates
	// Note that TypeScript expects the items to always be defined but that's not the case
	author.birthDate = ((psr.item.claims[P_DOB]?.[0] ?? '') as string).replace(/T.*/, '')
	author.deathDate = ((psr.item.claims[P_DOD]?.[0] ?? '') as string).replace(/T.*/, '')

	return author
}

function simplifyClaims(claims: SimplifiedClaims, prop: PropertyId,
		resolved: Record<string, SimplifiedEntity>, language: string): string {
	return claims[prop]
		.map(
			id => withFallback((resolved[id as string] as SimplifiedItem).labels, language)
		).join(' ')
}
