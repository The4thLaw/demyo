import { axiosGet } from '@/helpers/axios'

/**
 * Service to handle search requests.
 */
class SearchService {
	quicksearch(query: string) {
		console.debug('Searching for', query)
		return axiosGet('search/quick', { q: query }, [])
	}
}

export default new SearchService()
