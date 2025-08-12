<template>
	<div>
		<v-container fluid>
			<SectionCard :title="$t('title.index.taxonomy.GENRE')">
				<div class="v-TaxonIndex__list">
					<TaxonLink :model="genres" />
				</div>
			</SectionCard>

			<SectionCard :title="$t('title.index.taxonomy.TAG')">
				<div class="v-TaxonIndex__list">
					<TaxonLink :model="tags" />
				</div>
			</SectionCard>
		</v-container>

		<v-speed-dial
			location="bottom center"
		>
			<template #activator="{ props: activatorProps }">
				<!-- Vuetify's own FAB doesn't work for some reason: the options overlay the base button -->
				<Fab v-bind="activatorProps" icon="mdi-plus" />
			</template>

			<v-btn key="1" to="/genres/new" icon>
				<v-icon>mdi-drama-masks</v-icon>
				<v-tooltip :text="$t('menu.genres.add')" activator="parent" />
			</v-btn>
			<v-btn key="2" to="/tags/new" icon>
				<v-icon>mdi-tag</v-icon>
				<v-tooltip :text="$t('menu.tags.add')" activator="parent" />
			</v-btn>
		</v-speed-dial>
	</div>
</template>

<script setup lang="ts">
import { useSimpleIndex } from '@/composables/model-index'
import tagService from '@/services/taxon-service'

function getMaxUsageCount(tagsToCount: Taxon[], type: TaxonType): number {
	if (tagsToCount.length === 0) {
		return 0
	}

	const max = Math.max(...tagsToCount.filter(t => t.type === type).map(t => t.usageCount))
	// Have a reasonable minimum else it just looks silly if there are few tags and they are seldom used
	return Math.max(max, 10)
}

async function fetchData(): Promise<ProcessedTaxon[]> {
	const loadedTags = await tagService.findForIndex() as ProcessedTaxon[]

	// Post-process tags: compute the relative weight in %
	// (the base is 100% and the max is 200%, which is very convenient for the font-size)
	const maxUsageCount: Record<TaxonType, number> = {
		GENRE: 0,
		TAG: 0
	}
	maxUsageCount.GENRE = getMaxUsageCount(loadedTags, 'GENRE')
	maxUsageCount.TAG = getMaxUsageCount(loadedTags, 'TAG')
	for (const tag of loadedTags) {
		tag.relativeWeight = Math.round(100 * tag.usageCount / maxUsageCount[tag.type] + 100)
	}

	return Promise.resolve(loadedTags)
}

const { loading, modelList: taxons } = useSimpleIndex(tagService, 'title.index.taxonomy', fetchData)

const genres = computed(() => taxons.value.filter(t => t.type === 'GENRE'))
const tags = computed(() => taxons.value.filter(t => t.type === 'TAG'))
</script>

<style lang="scss">
.v-TaxonIndex__list {
	line-height: 275%;
	text-align: center;
}
</style>
