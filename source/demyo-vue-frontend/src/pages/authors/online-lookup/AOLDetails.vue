<template>
	<v-stepper-window-item
		:value="step"
	>
		<!-- TODO: label to ask to review -->
		<v-text-field v-model="author.firstName" :label="$t('field.Author.firstName')" />
		<v-text-field v-model="author.name" :label="$t('field.Author.name')" />
		<v-text-field v-model="author.nativeLanguageName" :label="$t('field.Author.nativeLanguageName')" />
		<Autocomplete v-model="author.country" :items="countries" label-key="field.Author.country" />
		<v-text-field v-model="author.birthDate" :label="$t('field.Author.birthDate')" type="date" />
		<v-text-field v-model="author.deathDate" :label="$t('field.Author.deathDate')" type="date" />
	</v-stepper-window-item>

	<!-- TODO: buttons in teleport -->
</template>

<script setup lang="ts">
import { useCountryList } from '@/helpers/countries'
import type { PeopleSearchResult } from '@/helpers/wikimedia/people-search'
import { loadPerson } from '@/helpers/wikimedia/people-search'

const props = defineProps<{
	step: number,
	psr: PeopleSearchResult | undefined,
	language: string
}>()
const emit = defineEmits<{
	confirm: [author: Partial<Author>]
}>()

const countries = useCountryList()
const author: Ref<Partial<Author>> = ref({})
void loadDetails()
watch(() => props.psr, loadDetails)

async function loadDetails(): Promise<void> {
	author.value = {}
	if (!props.psr) {
		return
	}
	author.value = await loadPerson(props.psr, props.language)
}

function confirm(): void {
	emit('confirm', author.value)
}

</script>
