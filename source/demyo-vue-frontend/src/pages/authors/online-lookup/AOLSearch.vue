<template>
	<v-stepper-window-item
		:value="step"
	>
		<div class="c-AOLSearch__searchTerm">
			<v-text-field
				v-model="searchTerm"
				:label="$t('page.AuthorOnlineLookup.searchTerm')"
				@update:model-value="resetPeopleSearch"
				@keydown.enter.stop.prevent="searchOnline"
			/>
			<v-btn
				color="secondary" prepend-icon="mdi-magnify"
				:loading="searchingPeople" @click="searchOnline"
			>
				{{ $t('page.AuthorOnlineLookup.doSearch') }}
			</v-btn>
		</div>

		<template v-if="hasSearched">
			<v-list
				v-if="peopleSearchResults.length > 0"
				activatable active-strategy="single-leaf" active-class="text-primary"
			>
				<v-list-item
					v-for="psr of peopleSearchResults" :key="psr.id"
					:title="psr.fullName"
					:subtitle="psr.description"
					@click="selectResult(psr)"
				/>
			</v-list>
			<v-alert
				v-else
				border="start" type="info" class="my-4" variant="outlined"
			>
				{{ $t('page.AuthorOnlineLookup.noSearchResult') }}
			</v-alert>
		</template>
	</v-stepper-window-item>
</template>

<script setup lang="ts">
import { searchPeople, type PeopleSearchResult } from '@/helpers/wikimedia/people-search'

const props = defineProps<{
	step: number,
	term: string,
	language: string
}>()

const emit = defineEmits<{
	select: [person: PeopleSearchResult]
	reset: []
}>()

const searchTerm = ref(props.term)

const hasSearched = ref(false)
const searchingPeople = ref(false)
const peopleSearchResults = ref([] as PeopleSearchResult[])

function autoSearch(): void {
	// Automatically search if we start with a term coming from the props
	if (props.term && props.term.trim().length > 0) {
		void searchOnline()
	}
}

autoSearch()

watch(() => props.term, t => {
	searchTerm.value = t
	autoSearch()
})

function resetPeopleSearch(): void {
	hasSearched.value = false
	peopleSearchResults.value = []
	emit('reset')
}

async function searchOnline(): Promise<void> {
	searchingPeople.value = true
	resetPeopleSearch()
	peopleSearchResults.value = await searchPeople(searchTerm.value, props.language)
	searchingPeople.value = false
	hasSearched.value = true
}

function selectResult(psr: PeopleSearchResult): void {
	emit('select', psr)
}
</script>

<style lang="scss">
.c-AOLSearch__searchTerm {
	display: flex;
}

@media screen and (width <= 600px) { // Vuetify "xs" breakpoint
	.c-AOLSearch__searchTerm {
		flex-direction: column;
	}
}
</style>
