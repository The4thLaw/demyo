<template>
	<div class="c-ReadingList">
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
	return readerService.findReadingList(id)
}

// It's not the index
const {modelList: albums} = useSimpleIndex(albumService, 'title.reader.readingList', fetchData);
</script>
