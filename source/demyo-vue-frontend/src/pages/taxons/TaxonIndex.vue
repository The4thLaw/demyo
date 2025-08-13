<template>
	<div>
		<v-container fluid>
			<SectionCard :loading="loading" :title="$t('title.index.taxonomy.GENRE')">
				<div class="v-TaxonIndex__list">
					<TaxonLink :model="genres" />
				</div>
			</SectionCard>

			<SectionCard :loading="loading" :title="$t('title.index.taxonomy.TAG')">
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
import { postProcessTaxons } from '@/composables/taxons'
import taxonService from '@/services/taxon-service'

async function fetchData(): Promise<ProcessedTaxon[]> {
	const loadedTaxons = await taxonService.findForIndex()
	return Promise.resolve(postProcessTaxons(loadedTaxons))
}

const { loading, modelList: taxons } = useSimpleIndex(taxonService, 'title.index.taxonomy', fetchData)

const genres = computed(() => taxons.value.filter(t => t.type === 'GENRE'))
const tags = computed(() => taxons.value.filter(t => t.type === 'TAG'))
</script>

<style lang="scss">
.v-TaxonIndex__list {
	line-height: 275%;
	text-align: center;
}
</style>
