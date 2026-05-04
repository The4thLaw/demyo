/**
 * This composable abstracts common features needed for splittable text pagination.
 */

import { useReaderStore } from '@/stores/reader'
import type { Dictionary } from 'lodash'
import deburr from 'lodash/deburr'
import groupBy from 'lodash/groupBy'
import { useDisplay } from 'vuetify'

const PAGE_CHANGE_EVENT = 'page-change'

function extractFirstLetter<T extends AbstractModel>(item: T, firstLetterExtractor: (item: T) => string): string {
	const first = deburr(firstLetterExtractor(item)).toUpperCase()
	if (/[A-Za-z]/.exec(first)) {
		return first
	} else if (/\d/.exec(first)) {
		return '0-9'
	}
	return '#'
}

export const emitTypes = [PAGE_CHANGE_EVENT]

interface PaginationState<T extends AbstractModel> {
	/** The current page number. */
	currentPage: Ref<number>
	/** The total page count. */
	pageCount: Ref<number>
	/** Whether a previous page is available. */
	hasPreviousPage: Ref<boolean>
	/** Whether a next page is available. */
	hasNextPage: Ref<boolean>
	/** The number of pages to display. */
	paginationVisible: Ref<number>
	/** The items, paginated. */
	paginatedItems: Ref<T[]>
	/** The items, paginated and grouped by first letter if relevant. */
	groupedItems: Ref<Dictionary<T[]>>
	/** Navigate to the previous page if possible. */
	previousPage: () => void
	/** Navigate to the next page if possible. */
	nextPage: () => void
}

export function useBasicPagination<T extends AbstractModel>(items: Ref<T[]>,
		itemsPerPage?: Ref<number>): PaginationState<T> {
	return usePagination(items, () => '#', () => undefined, itemsPerPage)
}

export function useResponsivePageCount(): Ref<number> {
	const display = useDisplay()
	return computed(() => {
		if (display.xs.value) {
			return 3
		}
		if (display.smAndDown.value) {
			return 5
		}
		return 10
	})
}

export function usePagination<T extends AbstractModel>(items: Ref<T[]>, firstLetterExtractor: (item: T) => string,
		emit: (evt: string) => void, itemsPerPage?: Ref<number>): PaginationState<T> {
	const currentPage = ref(1)

	const readerStore = useReaderStore()
	itemsPerPage ??= computed(() => readerStore.currentReader.configuration.pageSizeForText)

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
			emit(PAGE_CHANGE_EVENT)
		}
	}

	function nextPage(): void {
		if (hasNextPage.value) {
			currentPage.value++
			emit(PAGE_CHANGE_EVENT)
		}
	}

	return {
		currentPage,
		pageCount,
		hasPreviousPage,
		hasNextPage,
		paginationVisible: useResponsivePageCount(),

		paginatedItems,
		groupedItems,

		previousPage,
		nextPage
	}
}
