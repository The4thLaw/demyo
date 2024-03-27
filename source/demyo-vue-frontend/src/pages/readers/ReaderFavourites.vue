<template>
	<div class="c-ReaderFavourites">
		<MetaSeriesIndex :items="albums" />
	</div>
</template>

<script>
import MetaSeriesIndex from '@/components/MetaSeriesIndex.vue'
import modelViewMixin from '@/mixins/model-view'
import readerService from '@/services/reader-service'

export default {
	name: 'ReaderFavourites',

	components: {
		MetaSeriesIndex
	},

	// We can reuse the 'view' mixin even though it's not really a view : it is generic enough
	mixins: [modelViewMixin],

	data() {
		return {
			albums: []
		}
	},

	head() {
		return {
			title: this.$t('title.reader.favourites')
		}
	},

	methods: {
		async fetchData() {
			this.albums = await readerService.findFavouriteAlbums(this.parsedId)
		}
	}
}
</script>
