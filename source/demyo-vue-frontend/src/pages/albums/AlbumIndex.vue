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
import { useRoute } from 'vue-router'

const uiStore = useUiStore()
const route = useRoute()

const albums = ref([])

async function fetchData() {
	uiStore.enableGlobalOverlay()
	const filter = retrieveFilter(route)
	albums.value = await albumService.findForIndex(filter)
	uiStore.disableGlobalOverlay()
}

fetchData()
</script>
