<template>
	<v-stepper
		v-model="step"
	>
		<v-stepper-header>
			<v-stepper-item>Recherche</v-stepper-item>
			<v-divider />
			<v-stepper-item>Détails</v-stepper-item>
			<v-divider />
			<v-stepper-item>Biographie</v-stepper-item>
		</v-stepper-header>

		<v-stepper-window>
			<v-stepper-window-item
				:value="0"
			>
				<div class="c-AuthorLookup__searchTerm">
					<v-text-field
						v-model="searchTerm"
						:label="$t('page.AuthorOnlineLookup.searchTerm')"
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
						border="start" type="info" text class="my-4"
						variant="outlined"
					>
						{{ $t('page.AuthorOnlineLookup.noSearchResult') }}
					</v-alert>
				</template>
			</v-stepper-window-item>
		</v-stepper-window>
	</v-stepper>
</template>

<script setup lang="ts">
// TODO: refactor the panels in sub components
import type { PeopleSearchResult } from '@/helpers/wikimedia/people-search'
import { searchPeople } from '@/helpers/wikimedia/people-search'

const STEP_INIT = 0
const STEP_DETAILS = 1
const STEP_BIO = 2

const props = defineProps<{
	term: string
}>()
const searchTerm = ref(props.term)

const step = ref(STEP_INIT)
// TODO: dynamic
const language = ref('fr')
const hasSearched = ref(false)
// TODO: auto-search when opening if the term exists
const searchingPeople = ref(false)
const peopleSearchResults = ref([] as PeopleSearchResult[])
const selectedPerson = ref<PeopleSearchResult | undefined>(undefined)

watch(() => props.term, t => {
	searchTerm.value = t
})
watch(searchTerm, resetPeopleSearch)

function resetPeopleSearch(): void {
	hasSearched.value = false
	selectedPerson.value = undefined
	peopleSearchResults.value = []
}

async function searchOnline(): Promise<void> {
	resetPeopleSearch()
	peopleSearchResults.value = await searchPeople(searchTerm.value, language.value)
	searchingPeople.value = false
	hasSearched.value = true
}

function selectResult(psr: PeopleSearchResult): void {
	selectedPerson.value = psr
	step.value = STEP_DETAILS
}
</script>

<style lang="scss">
.c-AuthorLookup__searchTerm {
	display: flex;
}
</style>
