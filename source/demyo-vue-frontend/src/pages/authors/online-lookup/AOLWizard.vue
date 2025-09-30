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
				v-model="author"
				:step="STEP_DETAILS" :psr="selectedPerson" :language="language"
				@load="detailsLoaded = true"
			/>
			<AOLBio
				v-model="author"
				:step="STEP_BIO" :psr="selectedPerson" :language="language"
				@load="bioLoaded = true"
			/>
		</v-stepper-window>

		<v-stepper-actions
			:disabled="actionsDisabled"
			:prev-text="$t('page.AuthorOnlineLookup.step-action.back')"
			:next-text="nextText"
			@click:prev="prev"
			@click:next="next"
		/>
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
const detailsLoaded = ref(false)
const bioLoaded = ref(false)
const author = ref<Partial<Author>>({})

const nextText = computed(() => {
	switch (step.value) {
		case STEP_INIT:
			return i18n.t('page.AuthorOnlineLookup.step-action.next')
		case STEP_DETAILS:
			return i18n.t('page.AuthorOnlineLookup.step-action.confirm')
		case STEP_BIO:
			return i18n.t('page.AuthorOnlineLookup.step-action.apply')
		default:
			throw new Error('Unknown step ' + step.value)
	}
})

const isNextEnabled = computed(() => {
	if (step.value === STEP_INIT) {
		return selectedPerson.value !== undefined
	}
	if (step.value === STEP_DETAILS) {
		return detailsLoaded.value
	}
	if (step.value === STEP_BIO) {
		return bioLoaded.value
	}
	return false
})

const actionsDisabled = computed(() => {
	const isPrevEnabled = step.value > STEP_INIT
	if (isPrevEnabled && isNextEnabled.value) {
		return false
	}
	if (isPrevEnabled) {
		return 'next'
	}
	if (isNextEnabled.value) {
		return 'prev'
	}
	return true
})

function prev(): void {
	if (step.value > STEP_INIT) {
		step.value--
	}
}

function next(): void {
	if (!isNextEnabled.value) {
		return
	}
	if (step.value < STEP_BIO) {
		step.value++
	} else {
		// Send the author
		alert('OK author')
	}
}

function resetPerson(): void {
	selectedPerson.value = undefined
	detailsLoaded.value = false
	step.value = STEP_INIT
}

function selectPerson(psr: PeopleSearchResult): void {
	selectedPerson.value = psr
	step.value = STEP_DETAILS
}
</script>
