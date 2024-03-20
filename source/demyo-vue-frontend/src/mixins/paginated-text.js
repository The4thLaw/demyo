import { useReaderStore } from '@/stores/reader'
import deburr from 'lodash/deburr'
import groupBy from 'lodash/groupBy'
import { mapState } from 'pinia'

/**
 * This mixin abstracts common features needed for splittable text pagination.
 *
 * The extractFirstLetter method must be defined by the component.
 */
export default {
	props: {
		items: {
			type: Array,
			required: false,
			default: null
		}
	},

	mounted() {
		if (!(this.items || this.itemsToPaginate)) {
			console.error('This component has neither items nor itemsToPaginate')
		}
	},

	data() {
		return {
			currentPage: 1
		}
	},

	computed: {
		...mapState(useReaderStore, {
			itemsPerPage: store => store.currentReader.configuration.pageSizeForText
		}),

		// Allows overriding the list of items
		itemsToPaginate() {
			return this.items
		},

		paginatedItems() {
			return this.itemsToPaginate.slice(
				(this.currentPage - 1) * this.itemsPerPage, this.currentPage * this.itemsPerPage)
		},

		groupedItems() {
			return groupBy(this.paginatedItems, (i) => this.extractFirstLetter(i))
		},

		pageCount() {
			return Math.ceil(this.itemsToPaginate.length / this.itemsPerPage)
		},

		hasPreviousPage() {
			return this.currentPage > 1
		},

		hasNextPage() {
			return this.currentPage < this.pageCount
		}
	},

	methods: {
		previousPage() {
			if (this.hasPreviousPage) {
				this.currentPage--
				this.$emit('page-change')
			}
		},

		nextPage() {
			if (this.hasNextPage) {
				this.currentPage++
				this.$emit('page-change')
			}
		},

		extractFirstLetter(item) {
			/** @type String */
			const first = deburr(this.firstLetterExtractor(item)).toUpperCase()
			if (first.match(/[A-Za-z]/)) {
				return first
			} else if (first.match(/[0-9]/)) {
				return '0-9'
			}
			return '#'
		}
	}
}
