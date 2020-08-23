<template>
	<div>
		<GalleryIndex :items="derivatives" image-path="mainImage" @page-change="scrollToTop">
			<template v-slot:default="slotProps">
				<router-link :to="`/derivatives/${slotProps.item.id}/view`">
					<div v-if="slotProps.item.series">
						{{ slotProps.item.series.name }}
					</div>
					<div v-if="slotProps.item.album">
						{{ slotProps.item.album.title }}
					</div>
					<div v-if="slotProps.item.source">
						{{ slotProps.item.source.identifyingName }}
					</div>
				</router-link>
			</template>
		</GalleryIndex>
		<v-btn
			fab to="/derivatives/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import GalleryIndex from '@/components/GalleryIndex'
import { retrieveFilter } from '@/helpers/filter'
import derivativeService from '@/services/derivative-service'

export default {
	name: 'DerivativeIndex',

	components: {
		GalleryIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.derivative')
		}
	},

	data() {
		return {
			derivatives: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.$store.dispatch('ui/enableGlobalOverlay')
			let filter = retrieveFilter(this.$route)
			this.derivatives = await derivativeService.findForIndex(filter)
			this.$store.dispatch('ui/disableGlobalOverlay')
		}
	}
}
</script>
