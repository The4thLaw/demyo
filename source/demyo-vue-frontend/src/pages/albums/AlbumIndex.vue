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
import MetaSeriesIndex from '@/components/MetaSeriesIndex'
import { retrieveFilter } from '@/helpers/filter'
import albumService from '@/services/album-service'

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
			albums: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.$store.dispatch('ui/enableGlobalOverlay')
			const filter = retrieveFilter(this.$route)
			this.albums = await albumService.findForIndex(filter)
			this.$store.dispatch('ui/disableGlobalOverlay')
		}
	}
}
</script>
