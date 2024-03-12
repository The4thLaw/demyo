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
import SectionCard from '@/components/SectionCard.vue';
import quicksearch from '@/mixins/quicksearch';

//const uiStore = useUiStore()

export default {
	name: 'Home',

	components: {
		SectionCard
	},

	mixins: [quicksearch],

	metaInfo() {
		return {
			title: this.$t('title.home')
		}
	},

	created() {
		/*uiStore.disableSearch()
		uiStore.disableGlobalOverlay()*/
	}
}
</script>
