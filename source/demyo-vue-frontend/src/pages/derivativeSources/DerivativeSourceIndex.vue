<template>
	<div>
		<TextIndex :items="sources" :split-by-first-letter="false" @page-change="scrollToTop">
			<template #default="slotProps">
				<router-link :to="`/derivativeSources/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<v-btn
			fab to="/derivativeSources/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import TextIndex from '@/components/TextIndex'
import sourceService from '@/services/derivative-source-service'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'DerivativeSourceIndex',

	components: {
		TextIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.derivativeSource')
		}
	},

	data() {
		return {
			sources: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			const uiStore = useUiStore()
			uiStore.enableGlobalOverlay()
			this.sources = await sourceService.findForIndex()
			uiStore.disableGlobalOverlay()
		}
	}
}
</script>
