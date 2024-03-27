<template>
	<div class="c-ReadingList">
		<MetaSeriesIndex :items="albums" />
	</div>
</template>

<script>
import MetaSeriesIndex from '@/components/MetaSeriesIndex.vue'
import modelViewMixin from '@/mixins/model-view'
import readerService from '@/services/reader-service'

export default {
	name: 'ReadingList',

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
			title: this.$t('title.reader.readingList')
		}
	},

	methods: {
		async fetchData() {
			this.albums = await readerService.findReadingList(this.parsedId)
		}
	}
}
</script>
