import searchService from '@/services/search-service'
import debounce from 'lodash/debounce'

export function useQuicksearch() {
	const currentQuery = ref('')
	const lastQuery = ref('')
	const loading = ref(false)
	const results = ref<IModel[] | undefined>(undefined)

	const isRelevantSearchQuery = computed(() => currentQuery.value && currentQuery.value.length)

	function clearSearch() {
		currentQuery.value = ''
		lastQuery.value = ''
		results.value = undefined
	}

	function performSearch() {
		if (currentQuery.value === lastQuery.value) {
			// Avoids triggering the search on non-defining key strokes
			return
		}
		lastQuery.value = currentQuery.value
		results.value = undefined
		debouncedSearch()
	}

	const debouncedSearch = debounce(doPerformSearch, 300)

	async function doPerformSearch() {
		if (!isRelevantSearchQuery.value) {
			return
		}
		loading.value = true
		results.value = await searchService.quicksearch(currentQuery.value)
		loading.value = false
	}

	return {
		currentQuery,
		loading,
		results,
		isRelevantSearchQuery,
		clearSearch,
		performSearch
	}
}
