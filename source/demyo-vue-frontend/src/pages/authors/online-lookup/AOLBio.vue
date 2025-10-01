<template>
	<v-stepper-window-item
		:value="step"
	>
		<div v-if="loading" class="text-center">
			<v-progress-circular indeterminate color="primary" size="64" />
		</div>
		<v-alert
			v-else-if="excerpts.length === 0"
			border="start" type="info" class="my-4" variant="outlined"
		>
			{{ $t('page.AuthorOnlineLookup.noBiography') }}
		</v-alert>
		<template v-else>
			<p v-text="$t('page.AuthorOnlineLookup.selectBiography')" />
			<v-list
				activatable active-strategy="single-leaf" active-class="text-primary"
			>
				<v-list-item
					v-for="excerpt of excerpts" :key="excerpt.language" :lines="false"
					@click="author.biography = excerpt.excerpt"
				>
					<template #title>
						{{ $t('page.AuthorOnlineLookup.biographySource', { site: excerpt.title }) }}
					</template>
					<template #subtitle>
						<!-- v-html is safe here because we get a plain text value
							from Wikipedia and add the HTML ourselves -->
						<!-- eslint-disable-next-line vue/no-v-html -->
						<div v-html="excerpt.excerpt" />
					</template>
				</v-list-item>
			</v-list>
		</template>
	</v-stepper-window-item>
</template>

<script setup lang="ts">
import type { Excerpt } from '@/helpers/wikimedia/excerpt-search'
import { findExcerpts } from '@/helpers/wikimedia/excerpt-search'
import type { PeopleSearchResult } from '@/helpers/wikimedia/people-search'

const props = defineProps<{
	step: number,
	psr: PeopleSearchResult | undefined,
	language: string
}>()
const emit = defineEmits<{
	load: []
}>()

const loading = ref(true)
const author = defineModel<Partial<Author>>({ required: true })
const excerpts = ref<Excerpt[]>([])
void loadBiography()
watch(() => props.psr, loadBiography)

async function loadBiography(): Promise<void> {
	loading.value = true
	excerpts.value = []
	const pageTitles: Record<string, string> = {}
	const siteLinks = props.psr?.item.sitelinks ?? {}

	for (const [key, value] of Object.entries(siteLinks)) {
		if (/^..wiki$/.test(key)) {
			const lang = key.substring(0, 2)
			pageTitles[lang] = value
		}
	}

	excerpts.value = await findExcerpts(pageTitles, props.language)
	loading.value = false
	emit('load')
}
</script>
