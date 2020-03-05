<template>
	<div
		ref="keyTarget"
		v-touch="{
			left: nextPage,
			right: previousPage
		}"
		class="c-TextIndex"
		@keyup.arrow-left.exact="previousPage()"
		@keyup.arrow-right.exact="nextPage()"
	>
		<div v-if="!splitByFirstLetter">
			<v-card>
				<v-card-text>
					<v-list class="dem-columnized c-TextIndex__list" dense>
						<v-list-item v-for="item in paginatedItems" :key="item.id">
							<v-list-item-content>
								<slot :item="item" />
							</v-list-item-content>
						</v-list-item>
					</v-list>
				</v-card-text>
			</v-card>
		</div>

		<div v-if="splitByFirstLetter">
			<div v-for="(value, key) in groupedItems" :key="key">
				<div class="c-TextIndex__firstLetter display-1 mx-2 my-4 accent--text">
					{{ key }}
				</div>
				<v-card>
					<v-card-text>
						<v-list class="dem-columnized c-TextIndex__list" dense>
							<v-list-item v-for="item in value" :key="item.id">
								<v-list-item-content>
									<slot :item="item" />
								</v-list-item-content>
							</v-list-item>
						</v-list>
					</v-card-text>
				</v-card>
			</div>
		</div>
		<v-pagination
			v-if="pageCount > 1"
			v-model="currentPage"
			:length="pageCount"
			total-visible="10"
			class="my-2"
		/>
	</div>
</template>

<script>
import { groupBy, deburr } from 'lodash'
import { mapState } from 'vuex'
import { focusElement } from '@/helpers/dom'

export default {
	name: 'TextIndex',

	props: {
		items: {
			type: Array,
			required: true
		},
		splitByFirstLetter: {
			type: Boolean,
			default: true
		},
		firstLetterExtractor: {
			type: Function,
			default: () => '#'
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

		paginatedItems() {
			return this.items.slice((this.currentPage - 1) * this.itemsPerPage, this.currentPage * this.itemsPerPage)
		},

		groupedItems() {
			return groupBy(this.paginatedItems, (i) => this.extractFirstLetter(i))
		},

		pageCount() {
			return Math.ceil(this.items.length / this.itemsPerPage)
		}
	},

	mounted() {
		focusElement(this.$refs.keyTarget)
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
</script>

<style lang="less">
.c-TextIndex {
	// No outline on this artifically focused element
	outline: 0;
}

#demyo .c-TextIndex__firstLetter {
	font-family: serif !important;
}

#demyo .c-TextIndex__list {
	font-size: 1rem;
	column-count: initial;
	column-width: 15em;

	a {
		color: inherit;
		text-decoration: none;

		&:hover {
			color: var(--v-anchor-base);
			text-decoration: underline;
		}
	}
}
</style>
