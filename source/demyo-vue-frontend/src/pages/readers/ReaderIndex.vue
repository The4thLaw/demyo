<template>
	<div>
		<TextIndex :items="readers" :split-by-first-letter="false" @page-change="scrollToTop">
			<template #default="slotProps">
				<router-link :to="`/readers/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<v-btn
			fab to="/readers/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import TextIndex from '@/components/TextIndex.vue'
import readerService from '@/services/reader-service'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'ReaderIndex',

	components: {
		TextIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.readers')
		}
	},

	data() {
		return {
			readers: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			const uiStore = useUiStore()
			uiStore.enableGlobalOverlay()
			this.readers = await readerService.findForIndex()
			uiStore.disableGlobalOverlay()
		}
	}
}
</script>
