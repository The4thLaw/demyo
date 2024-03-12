<template>
	<div class="c-AlbumIndex">
		<MetaSeriesIndex :items="albums" @page-change="scrollToTop" />
		<v-btn
			fab to="/albums/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import MetaSeriesIndex from '@/components/MetaSeriesIndex.vue'
import { retrieveFilter } from '@/helpers/filter'
import albumService from '@/services/album-service'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'AlbumIndex',

	components: {
		MetaSeriesIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.album')
		}
	},

	data() {
		return {
			uiStore: useUiStore(),

			albums: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.uiStore.enableGlobalOverlay()
			const filter = retrieveFilter(this.$route)
			this.albums = await albumService.findForIndex(filter)
			this.uiStore.disableGlobalOverlay()
		}
	}
}
</script>
