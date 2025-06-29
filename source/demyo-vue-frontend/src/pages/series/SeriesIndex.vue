<template>
	<div>
		<TextIndex
			view-route="SeriesView" :items="modelList"
			:first-letter-extractor="(item: Series) => item.identifyingName[0]" @page-change="scrollToTop"
		/>
		<Fab to="/series/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import seriesService from '@/services/series-service'

const { modelList } = useSimpleIndex(seriesService, 'title.index.series')
</script>
