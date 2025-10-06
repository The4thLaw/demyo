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

	const mainClaims: SimplifiedClaims = psr.item.claims
	const subClaimIds = [
		...(mainClaims[P_GIVEN_NAME] ?? []) as EntityId[],
		...(mainClaims[P_FAMILY_NAME] ?? []) as EntityId[],
		...(mainClaims[P_CITIZENSHIP] ?? []) as EntityId[],
	]
	const languages = getLanguages(language)
	const subQuery = wdk.getEntities({ ids: subClaimIds, languages })
	const subEntities: Entities = (await axios.get<parse.WbGetEntitiesResponse>(subQuery)).data.entities
	const simpleSubClaims = wdk.simplify.entities(subEntities)

	const author: Partial<Author> = {}

	author.nativeLanguageName = getSingleClaim(mainClaims, P_NATIVE_NAME)
	author.name = resolveClaims(mainClaims, P_FAMILY_NAME, simpleSubClaims, language)
	author.firstName = resolveClaims(mainClaims, P_GIVEN_NAME, simpleSubClaims, language)
	if (author.nativeLanguageName
			&& ((author.nativeLanguageName === `${author.firstName} ${author.name}`.trim())
			|| (author.nativeLanguageName === `${author.name} ${author.firstName}`.trim()))) {
		// If the native language name is basically the first and last name, clear it, it brings no added value
		author.nativeLanguageName = ''
	}

	const citizenshipId = getSingleClaim(mainClaims, P_CITIZENSHIP)
	if (citizenshipId) {
		const citizenship = simpleSubClaims[citizenshipId] as SimplifiedItem
		author.country = getSingleClaim(citizenship.claims, P_ISO_3166_ALPHA_3)
	}

	// Strip the time parts of the dates
	author.birthDate = getSingleClaim(mainClaims, P_DOB).replace(/T.*/, '')
	author.deathDate = getSingleClaim(mainClaims, P_DOD).replace(/T.*/, '')

	return author
}

/**
 * Gets the first value matching a property in a set of claims.
 * @param claims The simplified claims.
 * @param property The property to get values for.
 * @returns The first value. If the claims are undefined or the property doesn't exist or the array of values is
 * empty, returns the empty string.
 */
function getSingleClaim(claims: SimplifiedClaims | undefined, property: string): string {
	if (!claims) {
		return ''
	}
	const claimValues = claims[property as PropertyId]
	const singleValue  = claimValues?.[0]
	return (singleValue ?? '') as string
}

function resolveClaims(mainClaims: SimplifiedClaims, prop: PropertyId,
		simpleSubClaims: Record<string, SimplifiedEntity>, language: string): string {
	if (!mainClaims[prop]) {
		return ''
	}
	return mainClaims[prop]
		.map(
			id => withFallback((simpleSubClaims[id as string] as SimplifiedItem).labels, language)
		).join(' ')
}
