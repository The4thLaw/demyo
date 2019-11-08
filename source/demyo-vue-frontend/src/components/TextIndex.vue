<template>
	<div>
		<div v-if="!splitByFirstLetter">
			<div v-for="item in items" :key="item.id">
				{{ firstLetterExtractor(item) }}<slot :item="item" />
			</div>
		</div>

		<div v-if="splitByFirstLetter">
			<div v-for="(value, key) in groupedItems" :key="key">
				{{key}}
				<div v-for="item in value" :key="item.id">
				   <slot :item="item" />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import { groupBy, deburr } from 'lodash'

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
			default: () => 'A'
		}
	},

	data() {
		return {
			itemsPerPage: 5,
			currentPage: 1
		}
	},

	computed: {
		paginatedItems() {
			return this.items.slice((this.currentPage-1)*this.itemsPerPage, this.currentPage*this.itemsPerPage)
		},

		groupedItems() {
			return groupBy(this.paginatedItems, (i) => deburr(this.firstLetterExtractor(i)))
		}
	}
}
</script>