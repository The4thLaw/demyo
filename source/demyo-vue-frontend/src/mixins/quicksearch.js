import QuickSearchResults from '@/components/QuickSearchResults.vue'
import searchService from '@/services/search-service'
import debounce from 'lodash/debounce'

export default {
	components: {
		QuickSearchResults
	},

	data() {
		return {
			quicksearchQuery: '',
			lastQuery: '',
			quicksearchLoading: false,
			quicksearchResults: undefined
		}
	},

	methods: {
		clearSearch() {
			this.quicksearchQuery = ''
			this.lastQuery = ''
			this.quicksearchResults = undefined
		},

		performSearch() {
			if (this.quicksearchQuery === this.lastQuery) {
				// Avoids triggering the search on non-defining key strokes
				return
			}
			this.lastQuery = this.quicksearchQuery
			this.quicksearchResults = undefined
			this.debouncedSearch()
		},

		debouncedSearch: debounce(
			function () {
				// eslint complains about "this" but it's valid in this context and it's even in the Vue docs
				// eslint-disable-next-line no-invalid-this
				this.doPerformSearch()
			}, 300),

		async doPerformSearch() {
			if (!this.isRelevantSearchQuery) {
				return
			}
			this.quicksearchLoading = true
			this.quicksearchResults = await searchService.quicksearch(this.quicksearchQuery)
			this.quicksearchLoading = false
		}
	},

	computed: {
		isRelevantSearchQuery() {
			return this.quicksearchQuery && this.quicksearchQuery.length >= 3
		}
	}
}
