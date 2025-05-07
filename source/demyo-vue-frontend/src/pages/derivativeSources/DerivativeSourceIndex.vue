<template>
	<div>
		<TextIndex
			view-route="DerivativeSourceView" :items="modelList"
			:first-letter-extractor="(item) => item.identifyingName[0]" @page-change="scrollToTop"
		/>
		<Fab to="/derivativeSources/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import sourceService from '@/services/derivative-source-service'

const { modelList } = useSimpleIndex(sourceService, 'title.index.derivativeSource')
</script>
