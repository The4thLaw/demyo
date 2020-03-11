import { debounce } from 'lodash'
import QuickSearchResults from '@/components/QuickSearchResults'
import searchService from '@/services/search-service'

export default {
	components: {
		QuickSearchResults
	},

	data() {
		return {
			quicksearchQuery: '',
			quicksearchLoading: false
		}
	},

	methods: {
		performSearch: debounce(
			function () {
				// eslint complains about "this" but it's valid in this context and it's even in the Vue docs
				this.doPerformSearch()
			}, 300),

		async doPerformSearch() {
			if (this.quicksearchQuery.length < 3) {
				return
			}
			this.quicksearchLoading = true
			this.quicksearchResults = await searchService.quicksearch(this.quicksearchQuery)
			this.quicksearchLoading = false
		}
	}
}
