<template>
	<div class="c-AlbumIndex">
		<MetaSeriesIndex :items="albums" @page-change="scrollToTop" />
		<Fab to="/albums/new" icon="mdi-plus" />
	</div>
</template>

<script setup>
import { retrieveFilter } from '@/helpers/filter'
import albumService from '@/services/album-service'
import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'

useHead({
	title: useI18n().t('title.index.album')
})

const uiStore = useUiStore()
const route = useRoute()

const albums = ref([])

async function fetchData() {
	uiStore.enableGlobalOverlay()
	const filter = retrieveFilter(route)
	albums.value = await albumService.findForIndex(filter)
	uiStore.disableGlobalOverlay()
}

void fetchData()
</script>
