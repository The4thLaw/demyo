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
		/>
	</div>
</template>

<script>
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

.c-CardTextIndex__panel {
	display: flex;
	flex-direction: row;
	flex-wrap: wrap;
	justify-content: space-between;
}

// TODO: below a certain media query, switch to 100% width
// Alternative: use a responsive 12-cell grid from Vuetify ?
// Alternative 2: use a responsive CSS grid, it will be able to align items properly in all cases
.c-CardTextIndex__item {
	flex: 1;
	min-width: 15em;
	max-width: 25em;
	margin-right: 1em;
	margin-bottom: 1em;
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
