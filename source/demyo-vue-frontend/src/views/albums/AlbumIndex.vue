<template>
	<div>
		<MetaSeriesIndex :items="albums" />
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
			this.albums = await albumService.findForIndex()
			this.$store.dispatch('ui/disableGlobalOverlay')
		}
	}
}
</script>
