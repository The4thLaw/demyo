<template>
	<div
		ref="key-target"
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
				<h2 class="c-CardTextIndex__firstLetter text-h4 mx-2 my-4 text-secondary">
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
			@update:model-value="emit('page-change')"
		/>
	</div>
</template>

<script setup lang="ts">
/**
 * This component is a text-based index that allows the caller to provide cards for the items.
 */

import { emitTypes, usePagination } from '@/composables/pagination'
import { focusElement } from '@/helpers/dom'
import { onMounted, useTemplateRef } from 'vue'

interface Props {
	items?: AbstractModel[]
	splitByFirstLetter?: boolean
	firstLetterExtractor?: (item: AbstractModel) => string
}
const props = withDefaults(defineProps<Props>(), {
	items: () => [],
	splitByFirstLetter: true,
	firstLetterExtractor: () => '#'
})

const keyTarget = useTemplateRef('key-target')
onMounted(() => focusElement(keyTarget.value))

const emit = defineEmits(emitTypes)

const { pageCount, currentPage, paginatedItems, groupedItems, previousPage, nextPage }
	= usePagination(toRef(() => props.items), props.firstLetterExtractor, emit)
</script>

<style lang="scss">
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
			color: rgb(var(--v-theme-secondary));
			text-decoration: underline;
		}
	}
}
</style>
