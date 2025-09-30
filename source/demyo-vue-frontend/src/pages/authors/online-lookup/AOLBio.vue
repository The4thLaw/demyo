<template>
	<v-stepper-window-item
		:value="step"
	>
		<div v-if="loading" class="text-center">
			<v-progress-circular indeterminate color="primary" size="64" />
		</div>
		<template v-else>
			<p v-text="$t('page.AuthorOnlineLookup.selectBiography')" />
			<v-list
				activatable active-strategy="single-leaf" active-class="text-primary"
			>
				<v-list-item
					v-for="excerpt of excerpts" :key="excerpt.language"
					@click="author.biography = excerpt.excerpt"
				>
					<div v-html="excerpt.excerpt"/>
				</v-list-item>
			</v-list>
			<v-btn v-if="hasFallbackExcerpt" color="secondary" variant="outlined" @click="translateBio">
				{{ $t('page.AuthorOnlineLookup.translateBio') }}
			</v-btn>
		</template>
	</v-stepper-window-item>
</template>

<script setup lang="ts">
import type { Excerpt } from '@/helpers/wikimedia/excerpt-search'
import { findExcerpts } from '@/helpers/wikimedia/excerpt-search'
import type { PeopleSearchResult } from '@/helpers/wikimedia/people-search'
import translate from 'google-translate-api-x'

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
	const siteLinks = props.psr?.item.sitelinks
	for (const key in siteLinks) {
		if (Object.hasOwn(siteLinks, key)) {
			if (/^..wiki$/.test(key)) {
				const lang = key.substring(0, 2)
				pageTitles[lang] = siteLinks[key] as string
			}
		}
	}
	excerpts.value = await findExcerpts(pageTitles, props.language)
	loading.value = false
	emit('load')
}

const hasFallbackExcerpt = computed(() => excerpts.value.find(e => e.language !== props.language) !== undefined)

async function translateBio() {
	const toTranslate = excerpts.value.find(e => e.language !== props.language)
	alert(toTranslate.excerpt)
	if (toTranslate) {
		// TODO: Remove this, Google always return 403
		alert(await translate(toTranslate?.excerpt, { from: toTranslate.language, to: props.language, client: 'gtx' }))
	}
}

</script>
