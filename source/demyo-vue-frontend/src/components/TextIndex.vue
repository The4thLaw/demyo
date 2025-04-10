<template>
	<div
		ref="key-target"
		v-touch="{
			left: nextPage,
			right: previousPage
		}"
		class="c-TextIndex"
		:class="{ 'c-TextIndex__compact': compact }"
		@keyup.arrow-left.exact="previousPage()"
		@keyup.arrow-right.exact="nextPage()"
	>
		<div v-if="!splitByFirstLetter">
			<v-card :flat="compact">
				<v-card-text>
					<v-list class="dem-columnized c-TextIndex__list" density="compact">
						<v-list-item v-for="item in paginatedItems" :key="item.id">
							<slot :item="item" />
						</v-list-item>
					</v-list>
				</v-card-text>
			</v-card>
		</div>

		<div v-if="splitByFirstLetter">
			<div v-for="(value, letter) in groupedItems" :key="letter">
				<h2 class="c-TextIndex__firstLetter text-h4 mx-2 my-4 text-secondary">
					{{ letter }}
				</h2>
				<v-card :flat="compact">
					<v-card-text>
						<v-list class="dem-columnized c-TextIndex__list" density="compact">
							<v-list-item v-for="item in value" :key="item.id">
								<slot :item="item" />
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
			@update:model-value="emit('page-change')"
		/>
	</div>
</template>

<script setup lang="ts">
import { emitTypes, usePagination } from '@/composables/pagination'
import { focusElement } from '@/helpers/dom'
import { useTemplateRef } from 'vue'

interface Props {
	items?: IModel[]
	splitByFirstLetter?: boolean,
	firstLetterExtractor?: (item: AbstractModel) => string,
	compact?: boolean
}
const props = withDefaults(defineProps<Props>(), {
	items: () => [],
	splitByFirstLetter: true,
	firstLetterExtractor: () => '#',
	compact: false
})

const keyTarget = useTemplateRef('key-target')
onMounted(() => focusElement(keyTarget.value))

const emit = defineEmits(emitTypes)

const { pageCount, currentPage, paginatedItems, groupedItems, previousPage, nextPage }
	= usePagination(toRef(() => props.items), props.firstLetterExtractor, emit, undefined)
</script>

<style lang="scss">
.c-TextIndex {
	// No outline on this artifically focused element
	outline: 0;
}

.c-TextIndex__compact {
	.v-card__text {
		padding: 0;
	}
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
			color: rgb(var(--v-theme-secondary));
			text-decoration: underline;
		}
	}
}
</style>
