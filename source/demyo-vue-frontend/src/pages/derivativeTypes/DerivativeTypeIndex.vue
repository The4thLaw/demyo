<template>
	<div>
		<TextIndex :items="types" :split-by-first-letter="false" @page-change="scrollToTop">
			<template #default="slotProps">
				<router-link :to="`/derivativeTypes/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<v-btn
			fab to="/derivativeTypes/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import TextIndex from '@/components/TextIndex.vue'
import typeService from '@/services/derivative-type-service'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'DerivativeTypeIndex',

	components: {
		TextIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.derivativeType')
		}
	},

	data() {
		return {
			types: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			const uiStore = useUiStore()
			uiStore.enableGlobalOverlay()
			this.types = await typeService.findForIndex()
			uiStore.disableGlobalOverlay()
		}
	}
}
</script>
