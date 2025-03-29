<template>
	<v-btn
		v-if="initialized" icon :loading="loading"
		variant="flat" color="primary"
		v-bind="$attrs" @click="toggle"
	>
		<v-icon v-if="isFavourite">
			mdi-heart
		</v-icon>
		<v-icon v-else>
			mdi-heart-outline
		</v-icon>
	</v-btn>
</template>

<script setup lang="ts">
import readerService from '@/services/reader-service'
import { useReaderStore } from '@/stores/reader'
import sortedIndexOf from 'lodash/sortedIndexOf'

const readerStore = useReaderStore()

const props = defineProps<{
	modelId: number,
	type: string
}>()

const initialized = ref(true)
const loading = ref(false)

const favourites = computed(() => {
	if (props.type === 'Album') {
		return readerStore.favouriteAlbums
	} else if (props.type === 'Series') {
		return readerStore.favouriteSeries
	}
	console.error('Invalid favourite type', props.type)
	return []
})

const isFavourite = computed(() => sortedIndexOf(favourites.value, props.modelId) > -1)

async function toggle() {
	loading.value = true
	// Call the right service method depending on type and state
	// The service should alter the store on its own and reactivity should propagate here
	if (isFavourite.value) {
		if (props.type === 'Album') {
			await readerService.removeFavouriteAlbum(props.modelId)
		} else if (props.type === 'Series') {
			await readerService.removeFavouriteSeries(props.modelId)
		}
	} else {
		if (props.type === 'Album') {
			await readerService.addFavouriteAlbum(props.modelId)
		} else if (props.type === 'Series') {
			await readerService.addFavouriteSeries(props.modelId)
		}
	}
	loading.value = false
}
</script>

