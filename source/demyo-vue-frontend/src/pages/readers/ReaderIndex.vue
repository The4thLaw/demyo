<template>
	<div>
		<TextIndex
			view-route="ReaderView" :items="modelList"
			:split-by-first-letter="false" @page-change="scrollToTop"
		/>
		<Fab to="/readers/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import readerService from '@/services/reader-service'

const { modelList } = useSimpleIndex(readerService, 'title.index.reader')
</script>
