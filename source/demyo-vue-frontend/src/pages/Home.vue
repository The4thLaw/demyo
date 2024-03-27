<template>
	<v-container id="v-Home">
		<SectionCard :title="$t('widget.search.title')">
			<p class="mt-8 mb-2" v-text="$t('widget.search.description')" />

			<v-text-field
				v-model="quicksearchQuery" clearable hide-details
				:placeholder="$t('widget.search.label')"
				prepend-icon="mdi-magnify" @keyup="performSearch"
				@click:clear="clearSearch"
			/>
		</SectionCard>

		<QuickSearchResults
			v-if="isRelevantSearchQuery" :results="quicksearchResults" :loading="quicksearchLoading"
		/>
	</v-container>
</template>

<script>
import quicksearch from '@/mixins/quicksearch'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'HomePage',

	mixins: [quicksearch],

	data() {
		return {
			uiStore: useUiStore()
		}
	},

	head() {
		return {
			title: this.$t('title.home')
		}
	},

	created() {
		this.uiStore.disableSearch()
		this.uiStore.disableGlobalOverlay()
	}
}
</script>
