<template>
	<ModelLink v-if="!loading" :model="entry" :view="view" :label="label" />
</template>

<script setup lang="ts">
import type AbstractModelService from '@/services/abstract-model-service'
import authorService from '@/services/author-service'
import seriesService from '@/services/series-service'

const props = defineProps<{
	modelId: number | string
	type: 'author' | 'series'
	label?: string
}>()
const loading = ref(true)
const entry = ref({} as IModel)
const view = ref('')

async function load(): Promise<void> {
	let service: AbstractModelService<Author> | AbstractModelService<Series>
	switch (props.type) {
		case 'author':
			service = authorService
			view.value = 'AuthorView'
			break
		case 'series':
			service = seriesService
			view.value = 'SeriesView'
			break
		default:
			throw new Error('Unsupported model type for dynamic linking', props.type)
	}
	entry.value = await service.findById(props.modelId as number)
	loading.value = false
}

void load()
</script>
