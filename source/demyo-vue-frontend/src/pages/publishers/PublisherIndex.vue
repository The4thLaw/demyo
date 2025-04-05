<template>
	<div>
		<CardTextIndex
			:items="modelList" :first-letter-extractor="(item) => item.identifyingName[0]"
			@page-change="scrollToTop"
		>
			<template #default="slotProps">
				<PublisherCard :publisher="slotProps.item" />
			</template>
		</CardTextIndex>
		<Fab to="/publishers/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import publisherService from '@/services/publisher-service'

const { modelList } = useSimpleIndex(publisherService, 'title.index.publisher')
</script>
