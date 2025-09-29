<template>
	<v-stepper
		v-model="step"
	>
		<v-stepper-header>
			<v-stepper-item icon="mdi-magnify">{{ $t('page.AuthorOnlineLookup.step.search') }}</v-stepper-item>
			<v-divider />
			<v-stepper-item icon="mdi-check">{{ $t('page.AuthorOnlineLookup.step.review') }}</v-stepper-item>
			<v-divider />
			<v-stepper-item icon="text-box">{{ $t('page.AuthorOnlineLookup.step.biography') }}</v-stepper-item>
		</v-stepper-header>

		<v-stepper-window>
			<AOLSearch
				:step="STEP_INIT" :term="term" :language="language"
				@select="selectPerson" @reset="resetPerson"
			/>
			<AOLDetails
				:step="STEP_DETAILS" :psr="selectedPerson" :language="language"
			/>
		</v-stepper-window>
	</v-stepper>
</template>

<script setup lang="ts">
import type { PeopleSearchResult } from '@/helpers/wikimedia/people-search'
import { useI18n } from 'vue-i18n'
import AOLSearch from './AOLSearch.vue'

const STEP_INIT = 0
const STEP_DETAILS = 1
const STEP_BIO = 2

defineProps<{
	term: string
}>()

const step = ref(STEP_INIT)
const i18n = useI18n()
const language = computed(() => i18n.locale.value.substring(0, 2))
const selectedPerson = ref<PeopleSearchResult | undefined>(undefined)

function resetPerson(): void {
	selectedPerson.value = undefined
	step.value = STEP_INIT
}

function selectPerson(psr: PeopleSearchResult): void {
	selectedPerson.value = psr
	step.value = STEP_DETAILS
}
</script>
