import { axiosGet } from '@/helpers/axios'

/**
 * Service to handle search requests.
 */
class SearchService {
	async quicksearch(query: string): Promise<IModel[]> {
		return axiosGet('search/quick', { q: query }, [])
	}
}

export default new SearchService()
