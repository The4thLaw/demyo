import axios from 'axios'

const FALLBACK_LANG = 'en'

interface WikipediaResponsePage {
	extract: string
}

interface WikipediaResponse {
	query: {
		pages: Record<string, WikipediaResponsePage>
	}
}

export interface Excerpt {
	language: string
	title: string
	excerpt: string
}

export async function findExcerpts(pageTitles: Record<string, string>, language: string): Promise<Excerpt[]> {
	const nominalP = findExcerptExact(pageTitles[language], language)
	let fallback
	if (language !== FALLBACK_LANG) {
		fallback = await findExcerptExact(pageTitles[FALLBACK_LANG], FALLBACK_LANG)
	}
	const nominal = await nominalP

	const excerpts = []
	if (nominal) {
		excerpts.push(nominal)
	}
	if (fallback) {
		excerpts.push(fallback)
	}
	return excerpts
}

async function findExcerptExact(pageTitle: string | undefined, language: string): Promise<Excerpt | undefined> {
	if (!pageTitle) {
		return undefined
	}

	console.debug(`Searching for the excerpt of pages named '${pageTitle}' in '${language}'`)

	const wikipediaExtractUrl
		= `https://${language}.wikipedia.org/w/api.php?action=query&format=json&titles=${pageTitle}`
		+ '&prop=extracts&exintro&explaintext&origin=*&redirects=1'
	const response = await axios.get<WikipediaResponse>(wikipediaExtractUrl)
	const pages = response.data.query.pages
	const firstPageId = Object.keys(pages)[0]
	let excerpt = pages[firstPageId].extract
	if (excerpt) {
		excerpt = `<p>${excerpt.replace(/(?:\r\n|\r|\n)/g, '</p><p>')}</p>`
		return {
			language,
			title: `${language}.wikipedia.org`,
			excerpt
		}
	}
	return undefined
}
