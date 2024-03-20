<template>
	<div>
		<TextIndex
			:items="modelList" :first-letter-extractor="(item) => item.identifyingName[0]"
			@page-change="scrollToTop"
		>
			<template #default="slotProps">
				<router-link :to="`/series/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<Fab to="/series/new" icon="mdi-plus" />
	</div>
</template>

<script setup>
import { useSimpleIndex } from '@/composables/model-index'
import seriesService from '@/services/series-service'

const { modelList } = useSimpleIndex(seriesService)
</script>
