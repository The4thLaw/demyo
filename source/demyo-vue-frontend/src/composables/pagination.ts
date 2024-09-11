/**
 * This composable abstracts common features needed for splittable text pagination.
 */

import { useReaderStore } from '@/stores/reader'
import { Dictionary } from 'lodash'
import deburr from 'lodash/deburr'
import groupBy from 'lodash/groupBy'

function extractFirstLetter<T extends AbstractModel>(item: T, firstLetterExtractor: (item: T) => string): string {
	const first = deburr(firstLetterExtractor(item)).toUpperCase()
	if (/[A-Za-z]/.exec(first)) {
		return first
	} else if (/[0-9]/.exec(first)) {
		return '0-9'
	}
	return '#'
}

export const emitTypes = ['page-change']

interface PaginationState<T> {
	/** The current page number. */
	currentPage: Ref<number>
	/** The total page count. */
	pageCount: Ref<number>
	/** Whether a previous page is available. */
	hasPreviousPage: Ref<boolean>
	/** Whether a next page is available. */
	hasNextPage: Ref<boolean>
	/** The items, paginated. */
	paginatedItems: Ref<T[]>
	/** The items, paginated and grouped by first letter if relevant. */
	groupedItems: Ref<Dictionary<T[]>>
	/** Navigate to the previous page if possible. */
	previousPage: () => void
	/** Navigate to the next page if possible. */
	nextPage: () => void
}

export function usePagination<T extends AbstractModel>(items: Ref<T[]>, firstLetterExtractor: (item: T) => string,
	emit: (evt: string) => void, itemsPerPage: Ref<number> | null): PaginationState<T> {
	const currentPage = ref(1)

	const readerStore = useReaderStore()
	if (!itemsPerPage) {
		itemsPerPage = computed(() => readerStore.currentReader.configuration.pageSizeForText)
	}

	const paginatedItems = computed(() => items.value.slice(
		(currentPage.value - 1) * itemsPerPage.value, currentPage.value * itemsPerPage.value))

	const groupedItems = computed(() =>
		groupBy(paginatedItems.value, (i) => extractFirstLetter(i, firstLetterExtractor)))

	const pageCount = computed(() => Math.ceil(items.value.length / itemsPerPage.value))
	const hasPreviousPage = computed(() => currentPage.value > 1)
	const hasNextPage = computed(() => currentPage.value < pageCount.value)

	function previousPage(): void {
		if (hasPreviousPage.value) {
			currentPage.value--
			emit('page-change')
		}
	}

	function nextPage(): void {
		if (hasNextPage.value) {
			currentPage.value++
			emit('page-change')
		}
	}

	return {
		currentPage,
		pageCount,
		hasPreviousPage,
		hasNextPage,

		paginatedItems,
		groupedItems,

		previousPage,
		nextPage
	}
}
