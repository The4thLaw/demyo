<template>
	<div class="c-AlbumIndex">
		<MetaSeriesIndex :items="albums" @page-change="scrollToTop" />
		<Fab v-if="!loading" to="/albums/new" icon="mdi-plus" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import { retrieveFilter } from '@/helpers/filter'
import albumService from '@/services/album-service'
import { useRoute } from 'vue-router'

const route = useRoute()

const filter = computed(() => retrieveFilter(route))

async function fetchData(): Promise<Album[]> {
	return albumService.findForIndex(filter.value)
}

const { loading, modelList: albums, load } = useSimpleIndex(albumService, 'title.index.album', fetchData)

watch(filter, load)
</script>
