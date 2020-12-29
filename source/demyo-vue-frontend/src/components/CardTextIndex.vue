<template>
	<div
		ref="keyTarget"
		v-touch="{
			left: nextPage,
			right: previousPage
		}"
		class="c-CardTextIndex"
		@keyup.arrow-left.exact="previousPage()"
		@keyup.arrow-right.exact="nextPage()"
	>
		<div v-if="!splitByFirstLetter" class="c-CardTextIndex__panel">
			<div v-for="item in paginatedItems" :key="item.id" class="c-CardTextIndex__item">
				<slot :item="item" />
			</div>
		</div>

		<div v-if="splitByFirstLetter">
			<div v-for="(value, letter) in groupedItems" :key="letter">
				<h2 class="c-CardTextIndex__firstLetter display-1 mx-2 my-4 accent--text">
					{{ letter }}
				</h2>
				<div class="c-CardTextIndex__panel">
					<div v-for="item in value" :key="item.id" class="c-CardTextIndex__item">
						<slot :item="item" />
					</div>
				</div>
			</div>
		</div>
		<v-pagination
			v-if="pageCount > 1"
			v-model="currentPage"
			:length="pageCount"
			total-visible="10"
			class="my-2"
			@input="$emit('page-change')"
		/>
	</div>
</template>

<script>
import { mapState } from 'vuex'
import { focusElement } from '@/helpers/dom'
import paginatedTextMixin from '@/mixins/paginated-text'

/**
 * This component is a text-based index that allows the caller to provide cards for the items.
 */
export default {
	name: 'CardTextIndex',

	mixins: [paginatedTextMixin],

	props: {
		splitByFirstLetter: {
			type: Boolean,
			default: true
		},
		firstLetterExtractor: {
			type: Function,
			default: () => '#'
		}
	},

	computed: {
		...mapState({
			itemsPerPage: state => state.reader.currentReader.configuration.pageSizeForCards
		})
	},

	mounted() {
		focusElement(this.$refs.keyTarget)
	}
}
</script>

<style lang="less">
.c-CardTextIndex {
	// No outline on this artifically focused element
	outline: 0;
}

#demyo .c-CardTextIndex__firstLetter {
	font-family: serif !important;
}

// A responsive grid of items that take at least 15em and grow if needed.
// Rows will be created of needed
.c-CardTextIndex__panel {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(15em, 1fr));
	grid-gap: 2em;
}

// Items will grow to take the full grid row height, to follow the Material Design advice of having scannable grids
.c-CardTextIndex__item {
	display: flex;
	flex-direction: column;

	& > * {
		flex: 1;
	}
}

#demyo .c-CardTextIndex__list {
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
