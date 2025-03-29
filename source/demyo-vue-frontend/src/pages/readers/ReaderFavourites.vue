<template>
	<div class="c-ReaderFavourites">
		<MetaSeriesIndex :items="albums" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import { getParsedId } from '@/helpers/route'
import albumService from '@/services/album-service'
import readerService from '@/services/reader-service'
import { useRoute } from 'vue-router'

const route = useRoute()

async function fetchData(): Promise<Album[]> {
	const id = getParsedId(route)
	return readerService.findFavouriteAlbums(id)
}

const { modelList: albums } = useSimpleIndex(albumService, 'title.reader.favourites', fetchData)
</script>
