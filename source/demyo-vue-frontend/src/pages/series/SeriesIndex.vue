<template>
	<div>
		<TextIndex
			:items="series" :first-letter-extractor="(item) => item.identifyingName[0]"
			@page-change="scrollToTop"
		>
			<template #default="slotProps">
				<router-link :to="`/series/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<v-btn
			fab to="/series/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import TextIndex from '@/components/TextIndex.vue'
import seriesService from '@/services/series-service'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'SeriesIndex',

	components: {
		TextIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.series')
		}
	},

	data() {
		return {
			series: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			const uiStore = useUiStore()
			uiStore.enableGlobalOverlay()
			this.series = await seriesService.findForIndex()
			uiStore.disableGlobalOverlay()
		}
	}
}
</script>
