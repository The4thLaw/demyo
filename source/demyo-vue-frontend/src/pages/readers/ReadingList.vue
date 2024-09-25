<template>
	<div class="c-ReadingList">
		<MetaSeriesIndex :items="albums" />
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import albumService from '@/services/album-service'
import readerService from '@/services/reader-service'
import { useRoute } from 'vue-router'

const route = useRoute()

async function fetchData(): Promise<Album[]> {
	let id
	if (route.params.id instanceof Array) {
		id = route.params.id[0]
	} else {
		id = route.params.id
	}
	return readerService.findReadingList(parseInt(id, 10))
}

// It's not the index
const {modelList: albums} = useSimpleIndex(albumService, 'title.reader.readingList', fetchData);
</script>
