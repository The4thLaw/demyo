<template>
	<ModelLink v-if="!loading" :model="entry" :view="view" :label="label" />
</template>

<script setup lang="ts">
import type AbstractModelService from '@/services/abstract-model-service'
import albumService from '@/services/album-service'
import authorService from '@/services/author-service'
import bindingService from '@/services/binding-service'
import bookTypeService from '@/services/book-type-service'
import collectionService from '@/services/collection-service'
import derivativeService from '@/services/derivative-service'
import derivativeSourceService from '@/services/derivative-source-service'
import derivativeTypeService from '@/services/derivative-type-service'
import publisherService from '@/services/publisher-service'
import seriesService from '@/services/series-service'

const props = defineProps<{
	modelId: number | string
	type: 'album' | 'author' | 'binding' | 'bookType' | 'collection' | 'derivative' | 'derivativeSource'
		| 'derivativeType' | 'publisher' | 'series'
	label?: string
}>()
const loading = ref(true)
const entry = ref({} as IModel)
const view = ref('')

async function load(): Promise<void> {
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	let service: AbstractModelService<any>
	switch (props.type) {
		case 'album':
			service = albumService
			view.value = 'AlbumView'
			break
		case 'author':
			service = authorService
			view.value = 'AuthorView'
			break
		case 'binding':
			service = bindingService
			view.value = 'BindingView'
			break
		case 'bookType':
			service = bookTypeService
			view.value = 'BookTypeView'
			break
		case 'collection':
			service = collectionService
			view.value = 'CollectionView'
			break
		case 'derivative':
			service = derivativeService
			view.value = 'DerivativeView'
			break
		case 'derivativeSource':
			service = derivativeSourceService
			view.value = 'DerivativeSourceView'
			break
		case 'derivativeType':
			service = derivativeTypeService
			view.value = 'DerivativeTypeView'
			break
		case 'publisher':
			service = publisherService
			view.value = 'PublisherView'
			break
		case 'series':
			service = seriesService
			view.value = 'SeriesView'
			break
		default:
			// eslint-disable-next-line @typescript-eslint/restrict-template-expressions
			throw new Error(`Unsupported model type for dynamic linking ${props.type}`)
	}
	// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
	entry.value = await service.findById(props.modelId as number)
	loading.value = false
}

void load()
</script>
