<template>
	<div>
		<TextIndex
			view-route="UniverseView" :items="modelList"
			:first-letter-extractor="(item: Universe) => item.identifyingName[0]" @page-change="scrollToTop"
		/>
		<Fab to="/series/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import universeService from '@/services/universe-service'

const { modelList } = useSimpleIndex(universeService, 'title.index.universes')
</script>
