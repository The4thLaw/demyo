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

<script setup lang="ts">
import { retrieveFilter } from '@/helpers/filter'
import derivativeService from '@/services/derivative-service'
import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'

useHead({
	title: useI18n().t('title.index.derivative')
})

const uiStore = useUiStore()
const route = useRoute()

const filter = computed(() => retrieveFilter(route))

const derivatives = ref([] as Derivative[])

async function fetchData(): Promise<void> {
	uiStore.enableGlobalOverlay()
	derivatives.value = await derivativeService.findForIndex(filter.value)
	uiStore.disableGlobalOverlay()
}

void fetchData()

watch(filter, fetchData)
</script>
