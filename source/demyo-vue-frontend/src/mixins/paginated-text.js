import { groupBy, deburr } from 'lodash'
import { mapState } from 'vuex'

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
		...mapState({
			itemsPerPage: state => state.reader.currentReader.configuration.pageSizeForText || 40
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
		}
	},

	methods: {
		previousPage() {
			if (this.currentPage > 1) {
				this.currentPage--
			}
		},

		nextPage() {
			if (this.currentPage < this.pageCount) {
				this.currentPage++
			}
		},

		extractFirstLetter(item) {
			/** @type String */
			let first = deburr(this.firstLetterExtractor(item))
			if (first.match(/[A-Za-z]/)) {
				return first
			} else if (first.match(/[0-9]/)) {
				return '0-9'
			}
			return '#'
		}
	}
}
