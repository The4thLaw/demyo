<template>
	<div class="c-MetaSeriesIndex">
		<CardTextIndex
			:items="items" :first-letter-extractor="firstLetterExtractor"
			@page-change="$emit('page-change')"
		>
			<template #default="slotProps">
				<MetaSeriesCard :meta="slotProps.item" />
			</template>
		</CardTextIndex>
	</div>
</template>

<script setup lang="ts">
import { emitTypes } from '@/composables/pagination'

defineProps<{
	items: MetaSeries[]
}>()

defineEmits(emitTypes)

function firstLetterExtractor(meta: MetaSeries): string {
	if (meta.series) {
		return meta.series.identifyingName[0]
	}
	if (meta.album) {
		return meta.album.title[0]
	}
	throw new Error('Item is neither a Series nor an Album: ' + JSON.stringify(meta))
}
</script>
