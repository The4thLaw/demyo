<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t(`quickTasks.edit.taxon.${taxon.type}`)"
				:to="`/taxons/${taxon.id}/edit`"
				icon="mdi-tag dem-overlay-edit"
			/>
			<AppTask
				:label="$t(`quickTasks.convert.taxon.${taxon.type}`)"
				icon="mdi-cached"
				@click="convertType"
			/>
			<!-- Note that we can always delete taxons -->
			<AppTask
				:label="$t(`quickTasks.delete.taxon.${taxon.type}`)"
				:confirm="$t(`quickTasks.delete.taxon.confirm.${taxon.type}`)"
				icon="mdi-tag dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="loading">
			<h1 v-if="!loading" class="text-h4">
				<span class="d-Taxon" :style="style">
					{{ taxon.identifyingName }}
				</span>
			</h1>
			<FieldValue :value="taxon.description" label-key="field.Taxon.description" type="rich-text" />
			<v-btn
				v-if="albumCount > 0"
				:to="{ name: 'AlbumIndex', query: { withTaxon: taxon.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t(`page.Taxon.viewAlbums.${taxon.type}`, albumCount) }}
			</v-btn>
			<v-alert
				v-if="albumCount === 0"
				border="start" type="info" text class="my-4"
				variant="outlined"
			>
				{{ $t(`page.Taxon.noAlbums.${taxon.type}`) }}
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import { useTaxonStyle } from '@/composables/taxons'
import taxonService from '@/services/taxon-service'

const albumCount = ref(-1)

async function fetchData(id: number): Promise<Taxon> {
	const taxonP = taxonService.findById(id)
	albumCount.value = await taxonService.countAlbums(id)
	return taxonP
}

const { model: taxon, appTasksMenu, loading, deleteModel, loadData } = useSimpleView(fetchData, taxonService,
	(t: Taxon) => `quickTasks.delete.tag.confirm.done.${t.type}`, 'TaxonIndex')

const style = useTaxonStyle(taxon)

async function convertType(): Promise<void> {
	appTasksMenu.value = false
	loading.value = true
	await taxonService.convertType(taxon.value.id)
	await loadData()
}
</script>
