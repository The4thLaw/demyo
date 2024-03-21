<template>
	<div>
		<GalleryIndex
			:items="derivatives" :keyboard-navigation="true"
			image-path="mainImage" @page-change="scrollToTop"
		>
			<template #default="slotProps">
				<router-link :to="`/derivatives/${slotProps.item.id}/view`">
					<div v-if="slotProps.item.series">
						{{ slotProps.item.series.identifyingName }}
					</div>
					<div v-if="slotProps.item.album">
						{{ slotProps.item.album.title }}
					</div>
					<div v-if="slotProps.item.source">
						{{ slotProps.item.source.identifyingName }}
					</div>
				</router-link>
			</template>
		</GalleryIndex>
		<Fab to="/derivatives/new" icon="mdi-plus" />
	</div>
</template>

<script setup>
import { retrieveFilter } from '@/helpers/filter'
import derivativeService from '@/services/derivative-service'
import { useUiStore } from '@/stores/ui'
import { useRoute } from 'vue-router'

const uiStore = useUiStore()
const route = useRoute()

const derivatives = ref([])

async function fetchData() {
	uiStore.enableGlobalOverlay()
	const filter = retrieveFilter(route)
	derivatives.value = await derivativeService.findForIndex(filter)
	uiStore.disableGlobalOverlay()
}

fetchData()
</script>
