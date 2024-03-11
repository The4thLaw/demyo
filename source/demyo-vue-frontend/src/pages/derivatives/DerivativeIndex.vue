<template>
	<div>
		<GalleryIndex
			:items="derivatives" :keyboard-navigation="true"
			image-path="mainImage" @page-change="scrollToTop"
		>
			<template #default="slotProps">
				<router-link :to="`/derivatives/${slotProps.item.id}/view`">
					<div v-if="slotProps.item.series">
						{{ slotProps.item.series.identifyingName }}
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
import { useUiStore } from '@/stores/ui'

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
			const uiStore = useUiStore()
			uiStore.enableGlobalOverlay()
			const filter = retrieveFilter(this.$route)
			this.derivatives = await derivativeService.findForIndex(filter)
			uiStore.disableGlobalOverlay()
		}
	}
}
</script>
