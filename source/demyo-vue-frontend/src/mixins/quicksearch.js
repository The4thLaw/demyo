import { debounce } from 'lodash'
import QuickSearchResults from '@/components/QuickSearchResults'

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

		doPerformSearch() {
			if (this.quicksearchQuery.length < 3) {
				return
			}
			this.quicksearchLoading = true
			console.log('Searching for', this.quicksearchQuery)
			// TODO: actual search and show results. Show progress while search is running
			this.quicksearchLoading = false
		}
	}
}
