<template>
	<v-stepper-window-item
		:value="step"
	>
		<div v-if="loading" class="text-center">
			<v-progress-circular indeterminate color="primary" size="64" />
		</div>
		<template v-else>
			<p v-text="$t('page.AuthorOnlineLookup.reviewDetails')" />

			<v-text-field v-model="author.firstName" :label="$t('field.Author.firstName')" />
			<v-text-field v-model="author.name" :label="$t('field.Author.name')" />
			<v-text-field v-model="author.nativeLanguageName" :label="$t('field.Author.nativeLanguageName')" />
			<Autocomplete v-model="author.country" :items="countries" label-key="field.Author.country" />
			<v-text-field v-model="author.birthDate" :label="$t('field.Author.birthDate')" type="date" />
			<v-text-field v-model="author.deathDate" :label="$t('field.Author.deathDate')" type="date" />
		</template>
	</v-stepper-window-item>
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
	load: []
}>()

const loading = ref(true)
const countries = useCountryList()
const author = defineModel<Partial<Author>>({ required: true })
void loadDetails()
watch(() => props.psr, loadDetails)

async function loadDetails(): Promise<void> {
	loading.value = true
	author.value = {}
	if (!props.psr) {
		loading.value = false
		return
	}
	author.value = await loadPerson(props.psr, props.language)
	loading.value = false
	emit('load')
}

</script>
