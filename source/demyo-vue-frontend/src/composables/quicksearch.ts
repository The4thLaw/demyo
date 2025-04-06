import searchService from '@/services/search-service'
import debounce from 'lodash/debounce'

interface Quicksearch {
	currentQuery: Ref<string>
	loading: Ref<boolean>
	results: Ref<IModel[] | undefined>
	isRelevantSearchQuery: Ref<boolean>
	clearSearch: () => void
	performSearch: () => Promise<void>
}

export function useQuicksearch(): Quicksearch {
	const currentQuery = ref('')
	const lastQuery = ref('')
	const loading = ref(false)
	const results = ref<IModel[] | undefined>(undefined)

	const isRelevantSearchQuery = computed(() => !!currentQuery.value.length)

	function clearSearch(): void {
		currentQuery.value = ''
		lastQuery.value = ''
		results.value = undefined
	}

	const debouncedSearch = debounce(doPerformSearch, 300)

	async function doPerformSearch(): Promise<void> {
		if (!isRelevantSearchQuery.value) {
			return
		}
		loading.value = true
		results.value = await searchService.quicksearch(currentQuery.value)
		loading.value = false
	}

	async function performSearch(): Promise<void> {
		if (currentQuery.value === lastQuery.value) {
			// Avoids triggering the search on non-defining key strokes
			return
		}
		lastQuery.value = currentQuery.value
		results.value = undefined
		await debouncedSearch()
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
