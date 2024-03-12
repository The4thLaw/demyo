<template>
	<div>
		<TextIndex :items="authors" :first-letter-extractor="(item) => item.name[0]" @page-change="scrollToTop">
			<template #default="slotProps">
				<router-link :to="`/authors/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<v-btn
			fab to="/authors/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import TextIndex from '@/components/TextIndex.vue'
import authorService from '@/services/author-service'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'AuthorIndex',

	components: {
		TextIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.author')
		}
	},

	data() {
		return {
			authors: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			const uiStore = useUiStore()
			uiStore.enableGlobalOverlay()
			this.authors = await authorService.findForIndex()
			uiStore.disableGlobalOverlay()
		}
	}
}
</script>
