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

<script setup lang="ts">
import { useQuicksearch } from '@/composables/quicksearch'
import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { useI18n } from 'vue-i18n'

const uiStore = useUiStore()
uiStore.disableSearch()
uiStore.disableGlobalOverlay()

const i18n = useI18n()
useHead({
	title: computed(() => i18n.t('title.home'))
})

const {
	currentQuery: quicksearchQuery, performSearch, clearSearch, isRelevantSearchQuery, loading: quicksearchLoading,
	results: quicksearchResults
}
	= useQuicksearch()
</script>
